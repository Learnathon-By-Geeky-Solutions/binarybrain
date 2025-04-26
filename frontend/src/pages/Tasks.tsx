import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
    Box,
    Button,
    Card,
    CardContent,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField,
    Typography,
    Grid,
    IconButton,
    Chip,
    MenuItem,
    Tab,
    Tabs,
    Alert,
    Paper,
    Rating,
} from '@mui/material';
import {
    Add as AddIcon,
    Edit as EditIcon,
    Delete as DeleteIcon,
    Assessment as AssessmentIcon,
} from '@mui/icons-material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { format } from 'date-fns';
import { taskService } from '../services/taskService';
import { courseService } from '../services/courseService';
import { RootState } from '../store';
import {
    Task,
    CreateTaskDto,
    UpdateTaskDto,
    TaskSubmission,
    CreateSubmissionDto,
    GradeSubmissionDto,
} from '../interfaces/task';
import { Course } from '../interfaces/course';

interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index }) => (
    <div role="tabpanel" hidden={value !== index}>
        {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
);

const taskValidationSchema = Yup.object({
    title: Yup.string().required('Title is required'),
    description: Yup.string().required('Description is required'),
    dueDate: Yup.string().required('Due date is required'),
    courseId: Yup.number().required('Course is required'),
});

const submissionValidationSchema = Yup.object({
    content: Yup.string().required('Submission content is required'),
});

const gradeValidationSchema = Yup.object({
    grade: Yup.number()
        .min(0, 'Grade must be at least 0')
        .max(100, 'Grade cannot exceed 100')
        .required('Grade is required'),
    feedback: Yup.string().required('Feedback is required'),
});

const Tasks: React.FC = () => {
    const user = useSelector((state: RootState) => state.auth.user);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [courses, setCourses] = useState<Course[]>([]);
    const [submissions, setSubmissions] = useState<TaskSubmission[]>([]);
    const [selectedTask, setSelectedTask] = useState<Task | null>(null);
    const [selectedSubmission, setSelectedSubmission] = useState<TaskSubmission | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [tabValue, setTabValue] = useState(0);

    // Dialog states
    const [openCreateDialog, setOpenCreateDialog] = useState(false);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [openSubmitDialog, setOpenSubmitDialog] = useState(false);
    const [openGradeDialog, setOpenGradeDialog] = useState(false);

    const isTeacher = user?.roles.includes('TEACHER');
    const isAdmin = user?.roles.includes('ADMIN');

    const fetchTasks = async () => {
        try {
            setLoading(true);
            let fetchedTasks: Task[];
            if (isAdmin) {
                fetchedTasks = await taskService.getAllTasks();
            } else if (isTeacher) {
                fetchedTasks = await taskService.getTasksByTeacher(user!.id);
            } else {
                fetchedTasks = await taskService.getTasksByStatus('OPEN');
            }
            setTasks(fetchedTasks);

            // Fetch courses for task creation/editing
            if (isTeacher || isAdmin) {
                const fetchedCourses = await courseService.getAllCourses();
                setCourses(fetchedCourses);
            }
        } catch (err) {
            setError('Failed to fetch tasks');
            console.error('Error fetching tasks:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTasks();
    }, [user]);

    const handleCreateTask = async (values: CreateTaskDto) => {
        try {
            await taskService.createTask(values);
            setOpenCreateDialog(false);
            setSuccess('Task created successfully');
            fetchTasks();
        } catch (err) {
            setError('Failed to create task');
            console.error('Error creating task:', err);
        }
    };

    const handleUpdateTask = async (values: UpdateTaskDto) => {
        try {
            await taskService.updateTask(values.id, values);
            setOpenEditDialog(false);
            setSuccess('Task updated successfully');
            fetchTasks();
        } catch (err) {
            setError('Failed to update task');
            console.error('Error updating task:', err);
        }
    };

    const handleDeleteTask = async () => {
        if (!selectedTask) return;
        try {
            await taskService.deleteTask(selectedTask.id);
            setOpenDeleteDialog(false);
            setSuccess('Task deleted successfully');
            fetchTasks();
        } catch (err) {
            setError('Failed to delete task');
            console.error('Error deleting task:', err);
        }
    };

    const handleSubmitTask = async (values: CreateSubmissionDto) => {
        try {
            await taskService.createSubmission({
                ...values,
                taskId: selectedTask!.id,
            });
            setOpenSubmitDialog(false);
            setSuccess('Task submitted successfully');
            fetchTasks();
        } catch (err) {
            setError('Failed to submit task');
            console.error('Error submitting task:', err);
        }
    };

    const handleGradeSubmission = async (values: GradeSubmissionDto) => {
        if (!selectedSubmission) return;
        try {
            await taskService.gradeSubmission(selectedSubmission.id, values);
            setOpenGradeDialog(false);
            setSuccess('Submission graded successfully');
            fetchTasks();
        } catch (err) {
            setError('Failed to grade submission');
            console.error('Error grading submission:', err);
        }
    };

    const columns: GridColDef[] = [
        { field: 'title', headerName: 'Title', flex: 1 },
        { field: 'description', headerName: 'Description', flex: 2 },
        {
            field: 'dueDate',
            headerName: 'Due Date',
            width: 200,
            valueFormatter: (params) => format(new Date(params.value), 'PP'),
        },
        {
            field: 'status',
            headerName: 'Status',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value}
                    color={
                        params.value === 'OPEN'
                            ? 'success'
                            : params.value === 'CLOSED'
                            ? 'error'
                            : 'default'
                    }
                />
            ),
        },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 200,
            renderCell: (params) => (
                <Box>
                    {(isTeacher || isAdmin) && (
                        <>
                            <IconButton
                                color="primary"
                                onClick={() => {
                                    setSelectedTask(params.row);
                                    setOpenEditDialog(true);
                                }}
                            >
                                <EditIcon />
                            </IconButton>
                            <IconButton
                                color="error"
                                onClick={() => {
                                    setSelectedTask(params.row);
                                    setOpenDeleteDialog(true);
                                }}
                            >
                                <DeleteIcon />
                            </IconButton>
                            <IconButton
                                color="info"
                                onClick={async () => {
                                    setSelectedTask(params.row);
                                    try {
                                        const submissions = await taskService.getTaskSubmissions(params.row.id);
                                        setSubmissions(submissions);
                                        setTabValue(1); // Switch to submissions tab
                                    } catch (err) {
                                        setError('Failed to fetch submissions');
                                        console.error('Error fetching submissions:', err);
                                    }
                                }}
                            >
                                <AssessmentIcon />
                            </IconButton>
                        </>
                    )}
                    {!isTeacher && !isAdmin && params.row.status === 'OPEN' && (
                        <Button
                            variant="contained"
                            size="small"
                            onClick={() => {
                                setSelectedTask(params.row);
                                setOpenSubmitDialog(true);
                            }}
                        >
                            Submit
                        </Button>
                    )}
                </Box>
            ),
        },
    ];

    return (
        <Box sx={{ p: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4">Tasks</Typography>
                {(isTeacher || isAdmin) && (
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => setOpenCreateDialog(true)}
                    >
                        Create Task
                    </Button>
                )}
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                    {error}
                </Alert>
            )}
            {success && (
                <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
                    {success}
                </Alert>
            )}

            <Box sx={{ width: '100%' }}>
                <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <Tabs value={tabValue} onChange={(_, newValue) => setTabValue(newValue)}>
                        <Tab label="Tasks" />
                        <Tab label="Submissions" />
                    </Tabs>
                </Box>

                <TabPanel value={tabValue} index={0}>
                    <Card>
                        <CardContent>
                            <DataGrid
                                rows={tasks}
                                columns={columns}
                                loading={loading}
                                autoHeight
                                pagination
                                disableRowSelectionOnClick
                            />
                        </CardContent>
                    </Card>
                </TabPanel>

                <TabPanel value={tabValue} index={1}>
                    <Grid container spacing={2}>
                        {submissions.map((submission) => (
                            <Grid item xs={12} key={submission.id}>
                                <Paper sx={{ p: 2 }}>
                                    <Box display="flex" justifyContent="space-between" alignItems="center">
                                        <Box>
                                            <Typography variant="h6">
                                                Submission by Student ID: {submission.studentId}
                                            </Typography>
                                            <Typography color="textSecondary">
                                                Submitted: {format(new Date(submission.submittedAt), 'PPpp')}
                                            </Typography>
                                        </Box>
                                        {submission.status === 'GRADED' && (
                                            <Box textAlign="right">
                                                <Typography variant="h6">Grade: {submission.grade}%</Typography>
                                                <Rating
                                                    value={(submission.grade || 0) / 20}
                                                    readOnly
                                                    precision={0.5}
                                                />
                                            </Box>
                                        )}
                                    </Box>
                                    <Typography sx={{ mt: 2, mb: 2 }}>{submission.content}</Typography>
                                    {submission.status === 'GRADED' && submission.feedback && (
                                        <Box sx={{ mt: 2 }}>
                                            <Typography variant="subtitle1">Feedback:</Typography>
                                            <Typography>{submission.feedback}</Typography>
                                        </Box>
                                    )}
                                    {(isTeacher || isAdmin) && submission.status === 'SUBMITTED' && (
                                        <Button
                                            variant="contained"
                                            onClick={() => {
                                                setSelectedSubmission(submission);
                                                setOpenGradeDialog(true);
                                            }}
                                        >
                                            Grade Submission
                                        </Button>
                                    )}
                                </Paper>
                            </Grid>
                        ))}
                    </Grid>
                </TabPanel>
            </Box>

            {/* Create Task Dialog */}
            <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)}>
                <DialogTitle>Create New Task</DialogTitle>
                <Formik
                    initialValues={{
                        title: '',
                        description: '',
                        dueDate: '',
                        courseId: '' as any,
                    }}
                    validationSchema={taskValidationSchema}
                    onSubmit={handleCreateTask}
                >
                    {({ values, errors, touched, handleChange, handleBlur }) => (
                        <Form>
                            <DialogContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="title"
                                            label="Title"
                                            value={values.title}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.title && !!errors.title}
                                            helperText={touched.title && errors.title}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="description"
                                            label="Description"
                                            multiline
                                            rows={4}
                                            value={values.description}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.description && !!errors.description}
                                            helperText={touched.description && errors.description}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="dueDate"
                                            label="Due Date"
                                            type="datetime-local"
                                            value={values.dueDate}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.dueDate && !!errors.dueDate}
                                            helperText={touched.dueDate && errors.dueDate}
                                            InputLabelProps={{
                                                shrink: true,
                                            }}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            select
                                            fullWidth
                                            name="courseId"
                                            label="Course"
                                            value={values.courseId}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.courseId && !!errors.courseId}
                                            helperText={touched.courseId && errors.courseId ? String(errors.courseId) : undefined}
                                        >
                                            {courses.map((course) => (
                                                <MenuItem key={course.id} value={course.id}>
                                                    {course.title}
                                                </MenuItem>
                                            ))}
                                        </TextField>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => setOpenCreateDialog(false)}>Cancel</Button>
                                <Button type="submit" variant="contained">Create</Button>
                            </DialogActions>
                        </Form>
                    )}
                </Formik>
            </Dialog>

            {/* Edit Task Dialog */}
            <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
                <DialogTitle>Edit Task</DialogTitle>
                {selectedTask && (
                    <Formik
                        initialValues={{
                            id: selectedTask.id,
                            title: selectedTask.title,
                            description: selectedTask.description,
                            dueDate: selectedTask.dueDate,
                            status: selectedTask.status,
                        }}
                        validationSchema={taskValidationSchema.omit(['courseId'])}
                        onSubmit={handleUpdateTask}
                    >
                        {({ values, errors, touched, handleChange, handleBlur }) => (
                            <Form>
                                <DialogContent>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                name="title"
                                                label="Title"
                                                value={values.title}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                error={touched.title && !!errors.title}
                                                helperText={touched.title && errors.title}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                name="description"
                                                label="Description"
                                                multiline
                                                rows={4}
                                                value={values.description}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                error={touched.description && !!errors.description}
                                                helperText={touched.description && errors.description}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                name="dueDate"
                                                label="Due Date"
                                                type="datetime-local"
                                                value={values.dueDate}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                error={touched.dueDate && !!errors.dueDate}
                                                helperText={touched.dueDate && errors.dueDate}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                select
                                                fullWidth
                                                name="status"
                                                label="Status"
                                                value={values.status}
                                                onChange={handleChange}
                                            >
                                                <MenuItem value="OPEN">Open</MenuItem>
                                                <MenuItem value="CLOSED">Closed</MenuItem>
                                                <MenuItem value="DONE">Done</MenuItem>
                                            </TextField>
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                                <DialogActions>
                                    <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
                                    <Button type="submit" variant="contained">Update</Button>
                                </DialogActions>
                            </Form>
                        )}
                    </Formik>
                )}
            </Dialog>

            {/* Delete Confirmation Dialog */}
            <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
                <DialogTitle>Delete Task</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this task? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
                    <Button onClick={handleDeleteTask} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Submit Task Dialog */}
            <Dialog open={openSubmitDialog} onClose={() => setOpenSubmitDialog(false)}>
                <DialogTitle>Submit Task</DialogTitle>
                <Formik
                    initialValues={{ 
                        content: '',
                        taskId: selectedTask?.id || 0
                    }}
                    validationSchema={submissionValidationSchema}
                    onSubmit={handleSubmitTask}
                >
                    {({ values, errors, touched, handleChange, handleBlur }) => (
                        <Form>
                            <DialogContent>
                                <TextField
                                    fullWidth
                                    name="content"
                                    label="Submission Content"
                                    multiline
                                    rows={6}
                                    value={values.content}
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                    error={touched.content && !!errors.content}
                                    helperText={touched.content && errors.content}
                                />
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => setOpenSubmitDialog(false)}>Cancel</Button>
                                <Button type="submit" variant="contained">Submit</Button>
                            </DialogActions>
                        </Form>
                    )}
                </Formik>
            </Dialog>

            {/* Grade Submission Dialog */}
            <Dialog open={openGradeDialog} onClose={() => setOpenGradeDialog(false)}>
                <DialogTitle>Grade Submission</DialogTitle>
                <Formik
                    initialValues={{
                        submissionId: selectedSubmission?.id || 0,
                        grade: 0,
                        feedback: '',
                    }}
                    validationSchema={gradeValidationSchema}
                    onSubmit={handleGradeSubmission}
                >
                    {({ values, errors, touched, handleChange, handleBlur }) => (
                        <Form>
                            <DialogContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="grade"
                                            label="Grade (0-100)"
                                            type="number"
                                            value={values.grade}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.grade && !!errors.grade}
                                            helperText={touched.grade && errors.grade}
                                            InputProps={{
                                                inputProps: { min: 0, max: 100 },
                                            }}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="feedback"
                                            label="Feedback"
                                            multiline
                                            rows={4}
                                            value={values.feedback}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.feedback && !!errors.feedback}
                                            helperText={touched.feedback && errors.feedback}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => setOpenGradeDialog(false)}>Cancel</Button>
                                <Button type="submit" variant="contained">Submit Grade</Button>
                            </DialogActions>
                        </Form>
                    )}
                </Formik>
            </Dialog>
        </Box>
    );
};

export default Tasks;
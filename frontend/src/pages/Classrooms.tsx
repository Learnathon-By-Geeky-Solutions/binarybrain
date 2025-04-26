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
    List,
    ListItem,
    ListItemText,
    ListItemSecondaryAction,
    Divider,
    Tab,
    Tabs,
    Alert,
} from '@mui/material';
import {
    Add as AddIcon,
    Edit as EditIcon,
    Delete as DeleteIcon,
    Group as GroupIcon,
    School as SchoolIcon,
} from '@mui/icons-material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { classroomService } from '../services/classroomService';
import { courseService } from '../services/courseService';
import { RootState } from '../store';
import { Classroom, CreateClassroomDto, UpdateClassroomDto } from '../interfaces/classroom';
import { Course } from '../interfaces/course';

const classroomValidationSchema = Yup.object({
    name: Yup.string().required('Name is required'),
    description: Yup.string().required('Description is required'),
});

interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

const TabPanel: React.FC<TabPanelProps> = ({ children, value, index }) => {
    return (
        <div role="tabpanel" hidden={value !== index}>
            {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
        </div>
    );
};

const Classrooms: React.FC = () => {
    const user = useSelector((state: RootState) => state.auth.user);
    const [classrooms, setClassrooms] = useState<Classroom[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    // Dialog states
    const [openCreateDialog, setOpenCreateDialog] = useState(false);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [openManageDialog, setOpenManageDialog] = useState(false);
    const [selectedClassroom, setSelectedClassroom] = useState<Classroom | null>(null);
    const [tabValue, setTabValue] = useState(0);

    // Management states
    const [availableStudents, setAvailableStudents] = useState<any[]>([]);
    const [availableCourses, setAvailableCourses] = useState<Course[]>([]);
    const [classroomStudents, setClassroomStudents] = useState<any[]>([]);
    const [classroomCourses, setClassroomCourses] = useState<Course[]>([]);

    const fetchClassrooms = async () => {
        try {
            setLoading(true);
            let fetchedClassrooms: Classroom[];
            if (user?.roles.includes('ADMIN')) {
                fetchedClassrooms = await classroomService.getAllClassrooms();
            } else if (user?.roles.includes('TEACHER')) {
                fetchedClassrooms = await classroomService.getClassroomsByTeacher(user.id);
            } else {
                fetchedClassrooms = await classroomService.getClassroomsByStudent(user!.id);
            }
            setClassrooms(fetchedClassrooms);
        } catch (err) {
            setError('Failed to fetch classrooms');
            console.error('Error fetching classrooms:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchClassrooms();
    }, [user]);

    const handleCreateClassroom = async (values: CreateClassroomDto) => {
        try {
            await classroomService.createClassroom(values);
            setOpenCreateDialog(false);
            setSuccess('Classroom created successfully');
            fetchClassrooms();
        } catch (err) {
            setError('Failed to create classroom');
            console.error('Error creating classroom:', err);
        }
    };

    const handleUpdateClassroom = async (values: UpdateClassroomDto) => {
        try {
            await classroomService.updateClassroom(values.id, values);
            setOpenEditDialog(false);
            setSuccess('Classroom updated successfully');
            fetchClassrooms();
        } catch (err) {
            setError('Failed to update classroom');
            console.error('Error updating classroom:', err);
        }
    };

    const handleDeleteClassroom = async () => {
        if (!selectedClassroom) return;
        try {
            await classroomService.deleteClassroom(selectedClassroom.id);
            setOpenDeleteDialog(false);
            setSuccess('Classroom deleted successfully');
            fetchClassrooms();
        } catch (err) {
            setError('Failed to delete classroom');
            console.error('Error deleting classroom:', err);
        }
    };

    const handleOpenManage = async (classroom: Classroom) => {
        setSelectedClassroom(classroom);
        setOpenManageDialog(true);
        // TODO: Fetch available students and courses
        try {
            const courses = await courseService.getAllCourses();
            setAvailableCourses(courses);
            // TODO: Fetch classroom students and courses
            const classroomCourses = await classroomService.getClassroomCourses(classroom.id);
            setClassroomCourses(classroomCourses);
        } catch (err) {
            setError('Failed to fetch management data');
            console.error('Error fetching management data:', err);
        }
    };

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setTabValue(newValue);
    };

    const columns: GridColDef[] = [
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'description', headerName: 'Description', flex: 2 },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 200,
            renderCell: (params) => (
                <Box>
                    <IconButton
                        color="primary"
                        onClick={() => {
                            setSelectedClassroom(params.row);
                            setOpenEditDialog(true);
                        }}
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        color="error"
                        onClick={() => {
                            setSelectedClassroom(params.row);
                            setOpenDeleteDialog(true);
                        }}
                    >
                        <DeleteIcon />
                    </IconButton>
                    <IconButton
                        color="secondary"
                        onClick={() => handleOpenManage(params.row)}
                    >
                        <GroupIcon />
                    </IconButton>
                </Box>
            ),
        },
    ];

    return (
        <Box sx={{ p: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4">Classrooms</Typography>
                {(user?.roles.includes('ADMIN') || user?.roles.includes('TEACHER')) && (
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => setOpenCreateDialog(true)}
                    >
                        Create Classroom
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

            <Card>
                <CardContent>
                    <DataGrid
                        rows={classrooms}
                        columns={columns}
                        loading={loading}
                        autoHeight
                        pagination
                        disableRowSelectionOnClick
                    />
                </CardContent>
            </Card>

            {/* Create Classroom Dialog */}
            <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)}>
                <DialogTitle>Create New Classroom</DialogTitle>
                <Formik
                    initialValues={{ name: '', description: '' }}
                    validationSchema={classroomValidationSchema}
                    onSubmit={handleCreateClassroom}
                >
                    {({ values, errors, touched, handleChange, handleBlur }) => (
                        <Form>
                            <DialogContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            name="name"
                                            label="Name"
                                            value={values.name}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            error={touched.name && !!errors.name}
                                            helperText={touched.name && errors.name}
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

            {/* Edit Classroom Dialog */}
            <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
                <DialogTitle>Edit Classroom</DialogTitle>
                {selectedClassroom && (
                    <Formik
                        initialValues={{
                            id: selectedClassroom.id,
                            name: selectedClassroom.name,
                            description: selectedClassroom.description,
                        }}
                        validationSchema={classroomValidationSchema}
                        onSubmit={handleUpdateClassroom}
                    >
                        {({ values, errors, touched, handleChange, handleBlur }) => (
                            <Form>
                                <DialogContent>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                name="name"
                                                label="Name"
                                                value={values.name}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                error={touched.name && !!errors.name}
                                                helperText={touched.name && errors.name}
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
                <DialogTitle>Delete Classroom</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this classroom? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
                    <Button onClick={handleDeleteClassroom} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Manage Classroom Dialog */}
            <Dialog
                open={openManageDialog}
                onClose={() => setOpenManageDialog(false)}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle>Manage Classroom</DialogTitle>
                <DialogContent>
                    <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <Tabs value={tabValue} onChange={handleTabChange}>
                            <Tab icon={<GroupIcon />} label="Students" />
                            <Tab icon={<SchoolIcon />} label="Courses" />
                        </Tabs>
                    </Box>

                    <TabPanel value={tabValue} index={0}>
                        <List>
                            {classroomStudents.map((student) => (
                                <React.Fragment key={student.id}>
                                    <ListItem>
                                        <ListItemText
                                            primary={`${student.firstName} ${student.lastName}`}
                                            secondary={student.email}
                                        />
                                        <ListItemSecondaryAction>
                                            <IconButton
                                                edge="end"
                                                color="error"
                                                onClick={() => {
                                                    // TODO: Implement remove student
                                                }}
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                    <Divider />
                                </React.Fragment>
                            ))}
                        </List>
                    </TabPanel>

                    <TabPanel value={tabValue} index={1}>
                        <List>
                            {classroomCourses.map((course) => (
                                <React.Fragment key={course.id}>
                                    <ListItem>
                                        <ListItemText
                                            primary={course.title}
                                            secondary={course.description}
                                        />
                                        <ListItemSecondaryAction>
                                            <IconButton
                                                edge="end"
                                                color="error"
                                                onClick={() => {
                                                    // TODO: Implement remove course
                                                }}
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                    <Divider />
                                </React.Fragment>
                            ))}
                        </List>
                    </TabPanel>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenManageDialog(false)}>Close</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default Classrooms;
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
    Chip,
    IconButton,
    MenuItem,
} from '@mui/material';
import {
    Add as AddIcon,
    Edit as EditIcon,
    Delete as DeleteIcon,
    Assignment as AssignmentIcon,
} from '@mui/icons-material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { courseService } from '../services/courseService';
import { Course, CreateCourseDto, UpdateCourseDto } from '../interfaces/course';
import { RootState } from '../store';

const courseValidationSchema = Yup.object({
    title: Yup.string().required('Title is required'),
    description: Yup.string().required('Description is required'),
});

const Courses: React.FC = () => {
    const user = useSelector((state: RootState) => state.auth.user);
    const [courses, setCourses] = useState<Course[]>([]);
    const [loading, setLoading] = useState(true);
    const [openCreateDialog, setOpenCreateDialog] = useState(false);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
    const [error, setError] = useState<string | null>(null);

    const fetchCourses = async () => {
        try {
            setLoading(true);
            let fetchedCourses: Course[];
            if (user?.roles.includes('ADMIN')) {
                fetchedCourses = await courseService.getAllCourses();
            } else {
                fetchedCourses = await courseService.getCoursesByAuthor(user!.id);
            }
            setCourses(fetchedCourses);
        } catch (err) {
            setError('Failed to fetch courses');
            console.error('Error fetching courses:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCourses();
    }, [user]);

    const handleCreateCourse = async (values: CreateCourseDto) => {
        try {
            await courseService.createCourse(values);
            setOpenCreateDialog(false);
            fetchCourses();
        } catch (err) {
            setError('Failed to create course');
            console.error('Error creating course:', err);
        }
    };

    const handleUpdateCourse = async (values: UpdateCourseDto) => {
        try {
            await courseService.updateCourse(values);
            setOpenEditDialog(false);
            fetchCourses();
        } catch (err) {
            setError('Failed to update course');
            console.error('Error updating course:', err);
        }
    };

    const handleDeleteCourse = async () => {
        if (!selectedCourse) return;
        try {
            await courseService.deleteCourse(selectedCourse.id);
            setOpenDeleteDialog(false);
            fetchCourses();
        } catch (err) {
            setError('Failed to delete course');
            console.error('Error deleting course:', err);
        }
    };

    const columns: GridColDef[] = [
        { field: 'title', headerName: 'Title', flex: 1 },
        { field: 'description', headerName: 'Description', flex: 2 },
        {
            field: 'status',
            headerName: 'Status',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value}
                    color={params.value === 'OPEN' ? 'success' : 'default'}
                />
            ),
        },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 200,
            renderCell: (params) => (
                <Box>
                    <IconButton
                        color="primary"
                        onClick={() => {
                            setSelectedCourse(params.row);
                            setOpenEditDialog(true);
                        }}
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        color="error"
                        onClick={() => {
                            setSelectedCourse(params.row);
                            setOpenDeleteDialog(true);
                        }}
                    >
                        <DeleteIcon />
                    </IconButton>
                    <IconButton
                        color="secondary"
                        onClick={() => {
                            // TODO: Implement task management
                        }}
                    >
                        <AssignmentIcon />
                    </IconButton>
                </Box>
            ),
        },
    ];

    return (
        <Box sx={{ p: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4">Courses</Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenCreateDialog(true)}
                >
                    Create Course
                </Button>
            </Box>

            <Card>
                <CardContent>
                    <DataGrid
                        rows={courses}
                        columns={columns}
                        loading={loading}
                        autoHeight
                        pagination
                        disableRowSelectionOnClick
                    />
                </CardContent>
            </Card>

            {/* Create Course Dialog */}
            <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)}>
                <DialogTitle>Create New Course</DialogTitle>
                <Formik
                    initialValues={{ title: '', description: '' }}
                    validationSchema={courseValidationSchema}
                    onSubmit={handleCreateCourse}
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

            {/* Edit Course Dialog */}
            <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
                <DialogTitle>Edit Course</DialogTitle>
                {selectedCourse && (
                    <Formik
                        initialValues={{
                            id: selectedCourse.id,
                            title: selectedCourse.title,
                            description: selectedCourse.description,
                            status: selectedCourse.status,
                        }}
                        validationSchema={courseValidationSchema}
                        onSubmit={handleUpdateCourse}
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
                                                select
                                                fullWidth
                                                name="status"
                                                label="Status"
                                                value={values.status}
                                                onChange={handleChange}
                                            >
                                                <MenuItem value="OPEN">Open</MenuItem>
                                                <MenuItem value="CLOSED">Closed</MenuItem>
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
                <DialogTitle>Delete Course</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this course? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
                    <Button onClick={handleDeleteCourse} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default Courses;
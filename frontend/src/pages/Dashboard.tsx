import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    CircularProgress,
    Box,
    Chip,
} from '@mui/material';
import { School, Class, Assignment } from '@mui/icons-material';
import { RootState } from '../store';
import { dashboardService } from '../services/dashboardService';
import { Course, Classroom, Task } from '../interfaces/dashboard';

const Dashboard: React.FC = () => {
    const user = useSelector((state: RootState) => state.auth.user);
    const [loading, setLoading] = useState(true);
    const [courses, setCourses] = useState<Course[]>([]);
    const [classrooms, setClassrooms] = useState<Classroom[]>([]);
    const [tasks, setTasks] = useState<Task[]>([]);

    useEffect(() => {
        const fetchDashboardData = async () => {
            if (!user) return;
            
            try {
                setLoading(true);
                const isTeacher = user.roles.includes('TEACHER');
                const isStudent = user.roles.includes('STUDENT');
                const isAdmin = user.roles.includes('ADMIN');

                // Fetch data based on user role
                if (isTeacher || isAdmin) {
                    const [courseData, classroomData, taskData] = await Promise.all([
                        isAdmin ? dashboardService.getCourses() : dashboardService.getCoursesByAuthor(user.id),
                        isAdmin ? dashboardService.getClassrooms() : dashboardService.getClassroomsByTeacher(user.id),
                        isAdmin ? dashboardService.getTasks() : dashboardService.getTasksByTeacher(user.id)
                    ]);
                    setCourses(courseData);
                    setClassrooms(classroomData);
                    setTasks(taskData);
                } else if (isStudent) {
                    const classroomData = await dashboardService.getClassroomsByStudent(user.id);
                    setClassrooms(classroomData);
                    // Get tasks from enrolled courses
                    const courseIds = Array.from(new Set(classroomData.flatMap(c => c.courses)));
                    const coursesData = await Promise.all(
                        courseIds.map(id => dashboardService.getCourses())
                    );
                    setCourses(coursesData.flat());
                    // Get open tasks
                    const tasksData = await dashboardService.getTasksByStatus('OPEN');
                    setTasks(tasksData);
                }
            } catch (error) {
                console.error('Error fetching dashboard data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [user]);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <div>
            <Typography variant="h4" gutterBottom>
                Welcome, {user?.firstName}!
            </Typography>
            <Grid container spacing={3}>
                {/* Courses Summary */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <School sx={{ mr: 1 }} />
                                <Typography variant="h6">Courses</Typography>
                            </Box>
                            <Typography variant="h3" component="div">
                                {courses.length}
                            </Typography>
                            <Box mt={2}>
                                {courses.slice(0, 3).map((course) => (
                                    <Chip
                                        key={course.id}
                                        label={course.title}
                                        color="primary"
                                        variant="outlined"
                                        sx={{ mr: 1, mb: 1 }}
                                    />
                                ))}
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Classrooms Summary */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <Class sx={{ mr: 1 }} />
                                <Typography variant="h6">Classrooms</Typography>
                            </Box>
                            <Typography variant="h3" component="div">
                                {classrooms.length}
                            </Typography>
                            <Box mt={2}>
                                {classrooms.slice(0, 3).map((classroom) => (
                                    <Chip
                                        key={classroom.id}
                                        label={classroom.name}
                                        color="secondary"
                                        variant="outlined"
                                        sx={{ mr: 1, mb: 1 }}
                                    />
                                ))}
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Tasks Summary */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <Assignment sx={{ mr: 1 }} />
                                <Typography variant="h6">Active Tasks</Typography>
                            </Box>
                            <Typography variant="h3" component="div">
                                {tasks.filter(task => task.status === 'OPEN').length}
                            </Typography>
                            <Box mt={2}>
                                {tasks
                                    .filter(task => task.status === 'OPEN')
                                    .slice(0, 3)
                                    .map((task) => (
                                        <Chip
                                            key={task.id}
                                            label={task.title}
                                            color="info"
                                            variant="outlined"
                                            sx={{ mr: 1, mb: 1 }}
                                        />
                                    ))}
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Recent Activity */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Recent Activity
                            </Typography>
                            <Typography color="textSecondary">
                                {tasks.length > 0
                                    ? `You have ${tasks.filter(task => task.status === 'OPEN').length} open tasks`
                                    : 'No recent activity'}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default Dashboard;
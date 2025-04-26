import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Container, Typography, Box, Alert, MenuItem } from '@mui/material';
import { register } from '../../store/slices/authSlice';
import { RootState, AppDispatch } from '../../store';

const validationSchema = Yup.object({
    firstName: Yup.string().required('First name is required'),
    lastName: Yup.string().required('Last name is required'),
    username: Yup.string().required('Username is required'),
    email: Yup.string().email('Invalid email address').required('Email is required'),
    password: Yup.string()
        .min(6, 'Password must be at least 6 characters')
        .required('Password is required'),
    roles: Yup.array().min(1, 'At least one role is required'),
    currentInstitute: Yup.string(),
    country: Yup.string(),
});

const roles = ['STUDENT', 'TEACHER'];

const Register: React.FC = () => {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();
    const { error, loading } = useSelector((state: RootState) => state.auth);

    const handleSubmit = async (values: any) => {
        try {
            await dispatch(register({
                ...values,
                roles: [values.roles], // Convert to array as backend expects array
            })).unwrap();
            navigate('/login');
        } catch (err) {
            // Error is handled by the redux slice
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h5">
                    Sign Up
                </Typography>
                {error && <Alert severity="error" sx={{ mt: 2, width: '100%' }}>{error}</Alert>}
                <Formik
                    initialValues={{
                        firstName: '',
                        lastName: '',
                        username: '',
                        email: '',
                        password: '',
                        roles: '',
                        currentInstitute: '',
                        country: '',
                    }}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    {({ values, errors, touched, handleChange, handleBlur }) => (
                        <Form style={{ width: '100%', marginTop: '1rem' }}>
                            <TextField
                                fullWidth
                                margin="normal"
                                label="First Name"
                                name="firstName"
                                value={values.firstName}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.firstName && !!errors.firstName}
                                helperText={touched.firstName && errors.firstName}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Last Name"
                                name="lastName"
                                value={values.lastName}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.lastName && !!errors.lastName}
                                helperText={touched.lastName && errors.lastName}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Username"
                                name="username"
                                value={values.username}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.username && !!errors.username}
                                helperText={touched.username && errors.username}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Email"
                                name="email"
                                type="email"
                                value={values.email}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.email && !!errors.email}
                                helperText={touched.email && errors.email}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Password"
                                name="password"
                                type="password"
                                value={values.password}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.password && !!errors.password}
                                helperText={touched.password && errors.password}
                            />
                            <TextField
                                select
                                fullWidth
                                margin="normal"
                                label="Role"
                                name="roles"
                                value={values.roles}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.roles && !!errors.roles}
                                helperText={touched.roles && errors.roles}
                            >
                                {roles.map((role) => (
                                    <MenuItem key={role} value={role}>
                                        {role}
                                    </MenuItem>
                                ))}
                            </TextField>
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Current Institute"
                                name="currentInstitute"
                                value={values.currentInstitute}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.currentInstitute && !!errors.currentInstitute}
                                helperText={touched.currentInstitute && errors.currentInstitute}
                            />
                            <TextField
                                fullWidth
                                margin="normal"
                                label="Country"
                                name="country"
                                value={values.country}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={touched.country && !!errors.country}
                                helperText={touched.country && errors.country}
                            />
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                color="primary"
                                disabled={loading}
                                sx={{ mt: 3, mb: 2 }}
                            >
                                {loading ? 'Signing up...' : 'Sign Up'}
                            </Button>
                            <Button
                                fullWidth
                                variant="text"
                                onClick={() => navigate('/login')}
                            >
                                Already have an account? Sign In
                            </Button>
                        </Form>
                    )}
                </Formik>
            </Box>
        </Container>
    );
};

export default Register;
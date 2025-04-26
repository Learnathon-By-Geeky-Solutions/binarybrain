import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import {
    Box,
    Card,
    CardContent,
    Grid,
    TextField,
    Button,
    Typography,
    Avatar,
    Alert,
    CircularProgress,
    Paper,
} from '@mui/material';
import { PhotoCamera } from '@mui/icons-material';
import { RootState } from '../store';
import { authService } from '../services/authService';

const validationSchema = Yup.object({
    firstName: Yup.string().required('First name is required'),
    lastName: Yup.string().required('Last name is required'),
    email: Yup.string().email('Invalid email address').required('Email is required'),
    currentInstitute: Yup.string(),
    country: Yup.string(),
});

const Profile: React.FC = () => {
    const user = useSelector((state: RootState) => state.auth.user);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handlePhotoUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        if (!event.target.files?.length || !user) return;

        const file = event.target.files[0];
        try {
            setLoading(true);
            setError(null);
            await authService.uploadPhoto(user.id, file);
            setSuccess('Profile photo updated successfully');
        } catch (err) {
            setError('Failed to upload profile photo');
            console.error('Error uploading photo:', err);
        } finally {
            setLoading(false);
        }
    };

    if (!user) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box sx={{ maxWidth: 800, mx: 'auto', py: 3 }}>
            <Typography variant="h4" gutterBottom>
                Profile
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}
            {success && (
                <Alert severity="success" sx={{ mb: 2 }}>
                    {success}
                </Alert>
            )}

            <Grid container spacing={3}>
                {/* Profile Photo Section */}
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 2, textAlign: 'center' }}>
                        <Avatar
                            src={user.profilePicture}
                            sx={{
                                width: 120,
                                height: 120,
                                mx: 'auto',
                                mb: 2,
                            }}
                        />
                        <input
                            accept="image/*"
                            style={{ display: 'none' }}
                            id="photo-upload"
                            type="file"
                            onChange={handlePhotoUpload}
                        />
                        <label htmlFor="photo-upload">
                            <Button
                                component="span"
                                variant="contained"
                                startIcon={<PhotoCamera />}
                                disabled={loading}
                            >
                                {loading ? 'Uploading...' : 'Change Photo'}
                            </Button>
                        </label>
                    </Paper>
                </Grid>

                {/* Profile Information Section */}
                <Grid item xs={12} md={8}>
                    <Card>
                        <CardContent>
                            <Formik
                                initialValues={{
                                    firstName: user.firstName,
                                    lastName: user.lastName,
                                    email: user.email,
                                    currentInstitute: user.currentInstitute || '',
                                    country: user.country || '',
                                }}
                                validationSchema={validationSchema}
                                onSubmit={async (values, { setSubmitting }) => {
                                    try {
                                        setError(null);
                                        setSuccess(null);
                                        // TODO: Implement update profile functionality
                                        setSuccess('Profile updated successfully');
                                    } catch (err) {
                                        setError('Failed to update profile');
                                        console.error('Error updating profile:', err);
                                    } finally {
                                        setSubmitting(false);
                                    }
                                }}
                            >
                                {({ values, errors, touched, handleChange, handleBlur, handleSubmit, isSubmitting }) => (
                                    <Form onSubmit={handleSubmit}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    fullWidth
                                                    name="firstName"
                                                    label="First Name"
                                                    value={values.firstName}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.firstName && !!errors.firstName}
                                                    helperText={touched.firstName && errors.firstName}
                                                />
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    fullWidth
                                                    name="lastName"
                                                    label="Last Name"
                                                    value={values.lastName}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.lastName && !!errors.lastName}
                                                    helperText={touched.lastName && errors.lastName}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    fullWidth
                                                    name="email"
                                                    label="Email"
                                                    value={values.email}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.email && !!errors.email}
                                                    helperText={touched.email && errors.email}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    fullWidth
                                                    name="currentInstitute"
                                                    label="Current Institute"
                                                    value={values.currentInstitute}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.currentInstitute && !!errors.currentInstitute}
                                                    helperText={touched.currentInstitute && errors.currentInstitute}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    fullWidth
                                                    name="country"
                                                    label="Country"
                                                    value={values.country}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.country && !!errors.country}
                                                    helperText={touched.country && errors.country}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Button
                                                    type="submit"
                                                    variant="contained"
                                                    disabled={isSubmitting}
                                                    sx={{ mt: 2 }}
                                                >
                                                    {isSubmitting ? (
                                                        <>
                                                            <CircularProgress size={24} sx={{ mr: 1 }} />
                                                            Updating...
                                                        </>
                                                    ) : (
                                                        'Update Profile'
                                                    )}
                                                </Button>
                                            </Grid>
                                        </Grid>
                                    </Form>
                                )}
                            </Formik>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Role Information */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Account Information
                            </Typography>
                            <Typography>
                                <strong>Username:</strong> {user.username}
                            </Typography>
                            <Typography>
                                <strong>Role:</strong> {user.roles.join(', ')}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Profile;
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
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
    IconButton,
    InputAdornment,
    Paper,
} from '@mui/material';
import {
    PhotoCamera,
    Visibility,
    VisibilityOff,
} from '@mui/icons-material';
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
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [showCurrentPassword, setShowCurrentPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);

    const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files?.length) {
            setSelectedFile(event.target.files[0]);
        }
    };

    const updateProfile = async (formData: FormData) => {
        try {
            if (user) {
                await authService.updateProfile(user.id, formData);
            }
        } catch (error) {
            throw error;
        }
    };

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
                                    currentPassword: '',
                                    newPassword: '',
                                }}
                                validationSchema={validationSchema}
                                onSubmit={async (values, { setSubmitting }) => {
                                    try {
                                        const formData = new FormData();
                                        formData.append('firstName', values.firstName);
                                        formData.append('lastName', values.lastName);
                                        formData.append('email', values.email);
                                        formData.append('currentInstitute', values.currentInstitute);
                                        formData.append('country', values.country);
                                        if (values.currentPassword) {
                                            formData.append('currentPassword', values.currentPassword);
                                            formData.append('newPassword', values.newPassword);
                                        }
                                        if (selectedFile) {
                                            formData.append('profilePicture', selectedFile);
                                        }
                                        await updateProfile(formData);
                                        setError(null);
                                        setSuccess('Profile updated successfully');
                                    } catch (err) {
                                        setSuccess(null);
                                        setError('Failed to update profile');
                                    } finally {
                                        setSubmitting(false);
                                    }
                                }}
                            >
                                {({ values, errors, touched, handleChange, handleBlur }) => (
                                    <Form>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <Box display="flex" flexDirection="column" alignItems="center">
                                                    <Avatar
                                                        src={user.profilePicture || undefined}
                                                        sx={{ width: 100, height: 100, mb: 2 }}
                                                    />
                                                    <input
                                                        accept="image/*"
                                                        style={{ display: 'none' }}
                                                        id="profile-picture"
                                                        type="file"
                                                        onChange={handleFileSelect}
                                                    />
                                                    <label htmlFor="profile-picture">
                                                        <Button variant="outlined" component="span">
                                                            Upload Picture
                                                        </Button>
                                                    </label>
                                                </Box>
                                            </Grid>
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
                                                    type="email"
                                                    value={values.email}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.email && !!errors.email}
                                                    helperText={touched.email && errors.email}
                                                />
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
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
                                            <Grid item xs={12} sm={6}>
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
                                                <Typography variant="h6" gutterBottom>
                                                    Change Password
                                                </Typography>
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    fullWidth
                                                    name="currentPassword"
                                                    label="Current Password"
                                                    type={showCurrentPassword ? 'text' : 'password'}
                                                    value={values.currentPassword}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.currentPassword && !!errors.currentPassword}
                                                    helperText={touched.currentPassword && errors.currentPassword}
                                                    InputProps={{
                                                        endAdornment: (
                                                            <InputAdornment position="end">
                                                                <IconButton
                                                                    onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                                                    edge="end"
                                                                >
                                                                    {showCurrentPassword ? <VisibilityOff /> : <Visibility />}
                                                                </IconButton>
                                                            </InputAdornment>
                                                        ),
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    fullWidth
                                                    name="newPassword"
                                                    label="New Password"
                                                    type={showNewPassword ? 'text' : 'password'}
                                                    value={values.newPassword}
                                                    onChange={handleChange}
                                                    onBlur={handleBlur}
                                                    error={touched.newPassword && !!errors.newPassword}
                                                    helperText={touched.newPassword && errors.newPassword}
                                                    InputProps={{
                                                        endAdornment: (
                                                            <InputAdornment position="end">
                                                                <IconButton
                                                                    onClick={() => setShowNewPassword(!showNewPassword)}
                                                                    edge="end"
                                                                >
                                                                    {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                                                </IconButton>
                                                            </InputAdornment>
                                                        ),
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box display="flex" justifyContent="flex-end">
                                                    <Button
                                                        type="submit"
                                                        variant="contained"
                                                        color="primary"
                                                    >
                                                        Save Changes
                                                    </Button>
                                                </Box>
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
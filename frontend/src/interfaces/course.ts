export interface Course {
    id: number;
    title: string;
    description: string;
    authorId: number;
    status: 'OPEN' | 'CLOSED';
    createdAt: string;
    updatedAt: string;
}

export interface CreateCourseDto {
    title: string;
    description: string;
}

export interface UpdateCourseDto {
    id: number;
    title: string;
    description: string;
    status: 'OPEN' | 'CLOSED';
}

export interface CourseFilters {
    status?: 'OPEN' | 'CLOSED';
    authorId?: number;
}
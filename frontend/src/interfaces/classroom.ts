export interface Classroom {
    id: number;
    name: string;
    description: string;
    teacherId: number;
    students: number[];
    courses: number[];
    createdAt: string;
    updatedAt: string;
}

export interface CreateClassroomDto {
    name: string;
    description: string;
}

export interface UpdateClassroomDto {
    id: number;
    name: string;
    description: string;
}

export interface ClassroomStudent {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
}

export interface ClassroomFilters {
    teacherId?: number;
    studentId?: number;
}
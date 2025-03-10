package com.onlineclassroom.management.mapper;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.model.User;

/**
 * The {@code UserMapper} class is responsible for converting between
 * {@link User} and {@link UserDto} objects.
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 * lastModified: 2025-02-04
 */
public class UserMapper {
    /**
     * Private constructor to prevent instantiation of this utility class (only provide static methods).
     *
     * <p>This class provides static mapping methods and should not be instantiated.
     * Attempting to create an instance of this class will result in a {@link RuntimeException}.
     */
    private UserMapper()  {
        throw new RuntimeException("This is a Utility class and can't be instantiated!");
    }

    /**
     * The {@code userToUserDtoMapper} method converts the properties of a {@link User} entity to a{@link UserDto}.
     *
     * @param user The {@link User} entity to be mapped to a {@link UserDto}.
     * @return A {@link UserDto} object with data from the {@link User} entity.
     */
    public static UserDto userToUserDtoMapper(User user){
        UserDto userDTO = new UserDto();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setCurrentInstitute(user.getCurrentInstitute());
        userDTO.setCountry(user.getCountry());
        userDTO.setGender(user.getGender());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());

        return userDTO;
    }

    /**
     * This method converts the properties of a {@link UserDto} into a
     * {@link User} entity, typically used for persisting data to the database.
     *
     * @param userDto The {@link UserDto} to be mapped to a {@link User} entity.
     * @return A {@link User} entity populated with the data from the {@link UserDto}.
     */
    public static User userDtoToUserMapper(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setCurrentInstitute(userDto.getCurrentInstitute());
        user.setCountry(userDto.getCountry());
        user.setGender(userDto.getGender());
        user.setProfilePicture(userDto.getProfilePicture());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // Password will be encoded in the service
        return user;
    }
}
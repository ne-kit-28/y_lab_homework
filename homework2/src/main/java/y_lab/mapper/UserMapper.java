package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.User;
import y_lab.dto.UserRequestDto;
import y_lab.dto.UserResponseDto;

import java.util.ArrayList;

/**
 * Mapper interface for converting between User and Data Transfer Objects (DTOs).
 * <p>
 * This interface uses MapStruct to generate the implementation for mapping
 * between User entities and UserRequestDto / UserResponseDto.
 * </p>
 */
@Mapper(imports = {y_lab.util.HashFunction.class})
public interface UserMapper {

    /**
     * Converts a UserRequestDto to a User entity.
     *
     * @param userRequestDto the UserRequestDto to be converted
     * @return the corresponding User entity
     */
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(userRequestDto.password()))")
    User userRequestDtoToUser(UserRequestDto userRequestDto);

    /**
     * Converts a User entity to a UserResponseDto.
     *
     * @param user the User entity to be converted
     * @return the corresponding UserResponseDto
     */
    @Mapping(target = "isBlock", expression = "java(user.isBlock())")
    UserResponseDto userToUserResponseDto(User user);

    /**
     * Converts a list of User entities to a list of UserResponseDto.
     *
     * @param users the list of User entities to be converted
     * @return a list of corresponding UserResponseDto
     */
    ArrayList<UserResponseDto> usersToUserResponseDtos(ArrayList<User> users);
}

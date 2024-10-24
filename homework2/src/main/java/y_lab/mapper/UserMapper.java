package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.User;
import y_lab.dto.UserRequestDto;
import y_lab.dto.UserResponseDto;

import java.util.ArrayList;

@Mapper(imports = {y_lab.util.HashFunction.class})
public interface UserMapper {
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(userRequestDto.password()))")
    User userRequestDtoToUser(UserRequestDto userRequestDto);

    @Mapping(target = "isBlock", expression = "java(user.isBlock())")
    UserResponseDto userToUserResponseDto(User user);
    ArrayList<UserResponseDto> usersToUserResponseDtos(ArrayList<User> users);
}

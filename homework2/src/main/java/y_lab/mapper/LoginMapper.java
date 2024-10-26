package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.User;
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResetDto;
import y_lab.dto.LoginUpDto;

@Mapper(imports = {y_lab.util.HashFunction.class})
public interface LoginMapper {
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(loginUpDto.password()))")
    User loginInDtoToUser(LoginInDto loginUpDto);
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(loginUpDto.password()))")
    User loginUpDtoToUser(LoginUpDto loginUpDto);
    @Mapping(target = "passwordHash", expression = "java(loginResetDto.password())")
    User loginResetDtoToUser(LoginResetDto loginResetDto);
}

package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.User;
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResetDto;
import y_lab.dto.LoginUpDto;

/**
 * Mapper interface for converting between User and login-related Data Transfer Objects (DTOs).
 * <p>
 * This interface uses MapStruct to generate the implementation for mapping
 * between User entities and LoginInDto, LoginUpDto, and LoginResetDto.
 * </p>
 */
@Mapper(imports = {y_lab.util.HashFunction.class})
public interface LoginMapper {

    /**
     * Converts a LoginInDto to a User entity.
     *
     * @param loginUpDto the LoginInDto to be converted
     * @return the corresponding User entity with the hashed password
     */
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(loginUpDto.password()))")
    User loginInDtoToUser(LoginInDto loginUpDto);

    /**
     * Converts a LoginUpDto to a User entity.
     *
     * @param loginUpDto the LoginUpDto to be converted
     * @return the corresponding User entity with the hashed password
     */
    @Mapping(target = "passwordHash", expression = "java(HashFunction.hashPassword(loginUpDto.password()))")
    User loginUpDtoToUser(LoginUpDto loginUpDto);

    /**
     * Converts a LoginResetDto to a User entity.
     *
     * @param loginResetDto the LoginResetDto to be converted
     * @return the corresponding User entity with the plain password (not hashed)
     */
    @Mapping(target = "passwordHash", expression = "java(loginResetDto.password())")
    User loginResetDtoToUser(LoginResetDto loginResetDto);
}

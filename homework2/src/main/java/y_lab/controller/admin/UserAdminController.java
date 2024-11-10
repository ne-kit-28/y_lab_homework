package y_lab.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import y_lab.domain.User;
import y_lab.out.audit.AuditRecord;
import y_lab.out.audit.AuditService;
import y_lab.service.UserService;

import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserAdminController {

    private final UserService userService;
    private final AuditService auditService;

    @Autowired
    public UserAdminController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @GetMapping(value = "/all")
    public ResponseEntity<ArrayList<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Block or unblock a user",
            description = "Blocks or unblocks a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User blocked/unblocked successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping(value = "/{userId}/block/{status}")
    public ResponseEntity<Void> blockUser(@PathVariable("userId") @Positive long userId,
                                          @PathVariable("status") boolean status) {
        if (userService.blockUser(userId, status))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get user audit records",
            description = "Retrieves audit records for a specific user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audit records retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found or no audit records available")
            })
    @GetMapping(value = "/{userId}/audit")
    public ResponseEntity<ArrayList<AuditRecord>> getAudit(@PathVariable("userId") @Positive long userId) {
        ArrayList<AuditRecord> auditRecord = auditService.getAudit(userId);
        if (!auditRecord.isEmpty())
            return ResponseEntity.ok(auditRecord);
        else
            return ResponseEntity.notFound().build();
    }
}

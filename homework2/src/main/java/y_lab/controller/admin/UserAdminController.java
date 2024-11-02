package y_lab.controller.admin;

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
import y_lab.out.audit.AuditServiceImpl;
import y_lab.service.serviceImpl.UserServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserAdminController {

    private final UserService userService;
    private final AuditService auditService;

    @Autowired
    public UserAdminController (UserServiceImpl userService, AuditServiceImpl auditService){
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<ArrayList<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping(value = "/{userId}/block/{status}")
    public ResponseEntity<Void> blockUser(@PathVariable("userId") @Positive long userId,
                                          @PathVariable("status") boolean status) {
        if (userService.blockUser(userId, status))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{userId}/audit")
    public ResponseEntity<ArrayList<AuditRecord>> getAudit(@PathVariable("userId") @Positive long userId) {
        ArrayList<AuditRecord> auditRecord = auditService.getAudit(userId);
        if (!auditRecord.isEmpty())
            return ResponseEntity.ok(auditRecord);
        else
            return ResponseEntity.notFound().build();
    }
}

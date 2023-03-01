package online.strongnation.security.controller;

import lombok.AllArgsConstructor;
import online.strongnation.business.model.dto.RegionDTO;
import online.strongnation.security.model.*;
import online.strongnation.security.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v2/developer")
@AllArgsConstructor
public class DeveloperController {

    private final AuthenticationService service;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('developer:create')")
    public ResponseEntity<AuthenticationResponse> create(@RequestBody UserDTO user) {
        AuthenticationResponse register = service.register(user, Role.DEVELOPER);
        return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('developer:read')")
    public ResponseEntity<List<String>> getEmails() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PutMapping("/update/password")
    @PreAuthorize("hasAuthority('developer:update')")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @RequestBody UpdatePasswordDTO passwordDTO) {
        String name = authentication.getName();
        return new ResponseEntity<>("name: " + name + "authorities: " + authentication.getAuthorities() + " userdto: " + passwordDTO, HttpStatus.OK);
    }

    @PutMapping("/update/email")
    @PreAuthorize("hasAuthority('developer:update')")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @RequestBody UpdateEmailDTO updateEmailDTO) {
        String name = authentication.getName();
        return new ResponseEntity<>("name: " + name + "authorities: " + authentication.getAuthorities() + " userdto: " + updateEmailDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasAuthority('developer:delete')")
    public ResponseEntity<RegionDTO> delete(Authentication authentication, @PathVariable("email") String email) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}

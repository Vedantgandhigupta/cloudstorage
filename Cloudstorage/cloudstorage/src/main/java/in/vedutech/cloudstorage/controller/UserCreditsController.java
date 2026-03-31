package in.vedutech.cloudstorage.controller;

import in.vedutech.cloudstorage.document.UserCredits;
import in.vedutech.cloudstorage.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCreditsController {

    private final UserCreditsService userCreditsService;

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits() {
        UserCredits credits = userCreditsService.getUserCredits();
        return ResponseEntity.ok(credits);
    }
}

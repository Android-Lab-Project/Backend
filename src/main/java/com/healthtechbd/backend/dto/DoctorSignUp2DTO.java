package  com.healthtechbd.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSignUp2DTO {
    private String firstName;
    private String email;
    private String password;
    private String contactNo;
    private String bio;
    private String currentHospital;
    private String place;
    private String degrees;
}

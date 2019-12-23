package fhq.demo.bean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author fhq
 * @date 2019/12/20 11:07
 */
@Setter
@Getter
public class User {
    @NotEmpty
    @Email
    private String email;

    @Size(min = 6)
    @NotEmpty
    private String password;

    private boolean isEmailActive;
}

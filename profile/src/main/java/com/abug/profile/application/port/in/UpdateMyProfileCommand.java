package com.abug.profile.application.port.in;

import java.time.LocalDate;

public record UpdateMyProfileCommand(String firstName, String lastName, LocalDate dob, String city) {
}

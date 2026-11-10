package com.abug.identity.application.port.in;

import java.util.Set;

public record RoleCommand(String name, String description, Set<String> permissions) {
}

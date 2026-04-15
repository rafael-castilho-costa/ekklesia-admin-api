package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/metadata/enums")
public class EnumMetadataController {

    @GetMapping("/persona-types")
    public List<EnumDto> personaTypes() {
        return toDto(PersonaType.values());
    }

    @GetMapping("/marital-statuses")
    public List<EnumDto> maritalStatuses() {
        return toDto(MaritalStatus.values());
    }

    @GetMapping("/ministries")
    public List<EnumDto> ministries() {
        return toDto(Ministry.values());
    }

    @GetMapping("/member-statuses")
    public List<EnumDto> memberStatuses() {
        return toDto(StatusMember.values());
    }

    private List<EnumDto> toDto(Enumerable[] values) {
        return java.util.Arrays.stream(values)
                .map(Enumerable::enumerable)
                .toList();
    }
}

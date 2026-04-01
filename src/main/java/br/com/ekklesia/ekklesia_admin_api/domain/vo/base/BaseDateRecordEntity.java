package br.com.ekklesia.ekklesia_admin_api.domain.vo.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter
@MappedSuperclass
public class BaseDateRecordEntity extends  BaseEntity {

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        BaseDateRecordEntity that = (BaseDateRecordEntity)  object;
        return Objects.equals(getId(), that.getId());
    }

    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt());
    }
}

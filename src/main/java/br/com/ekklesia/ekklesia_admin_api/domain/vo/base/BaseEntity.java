package br.com.ekklesia.ekklesia_admin_api.domain.vo.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public static BaseEntity from(Integer id) {
        var baseEntity = new BaseEntity();
        baseEntity.setId(id);
        return baseEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    public boolean equals(Object object) {
        BaseEntity other = (BaseEntity) object;

        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return  false;
        }
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }
}

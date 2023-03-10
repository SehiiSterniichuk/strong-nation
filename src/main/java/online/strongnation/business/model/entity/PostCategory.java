package online.strongnation.business.model.entity;

import jakarta.persistence.*;
import lombok.*;
import online.strongnation.business.model.statistic.CategoryHolder;
import online.strongnation.business.model.dto.CategoryDTO;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "post_category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "category_id"})
})
public class PostCategory implements CategoryHolder {

    @Id
    @SequenceGenerator(
            name = "post_category_sequence",
            sequenceName = "post_category_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_category_sequence"
    )
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity categoryEntity;

    public PostCategory(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public PostCategory(CategoryDTO categoryDTO) {
        this(new CategoryEntity(categoryDTO));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCategory that = (PostCategory) o;
        return categoryEntity.equals(that.categoryEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryEntity);
    }
}

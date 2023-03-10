package online.strongnation.unit.entity;

import online.strongnation.business.model.entity.*;
import online.strongnation.business.repository.*;
import online.strongnation.business.exception.RegionNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PostDataSavingTest {

    @Autowired
    private PostRepository repository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    @Autowired
    private RegionCategoryRepository regionCategoryRepository;
    @Autowired
    private PostPhotoRepository postPhotoRepository;
    @Autowired
    private CountryRepository countryRepository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void simpleSave() {
        Country country = new Country();
        country.setName("Ukraine");
        Region region = new Region();
        country.setRegions(List.of(region));
        final String name = "Rivne";
        region.setName(name);

        final Post post = new Post();
        final String heading = "first post some heading";
        post.setDescription(heading);
        post.setDate(LocalDateTime.now());
        final PostPhoto photo = new PostPhoto();
        final String path = "some/linux/path/like/normal/operation/system";
        photo.setRelativePathToPhoto(path);
        post.setPostPhoto(photo);
        final String link = "localH0sT";
        post.setLink(link);
        region.setPosts(List.of(post));
        countryRepository.save(country);

        final var savedRegion = regionRepository.findFirstByName(name).orElseThrow(() -> {
            throw new RegionNotFoundException();
        });
        assertThat(savedRegion).isEqualTo(region);
        assertThat(savedRegion.getPosts()).isNotNull();
        assertThat(savedRegion.getPosts().size()).isEqualTo(1);
        assertThat(savedRegion.getPosts().get(0)).isEqualTo(post);
        var postSaved = repository.findAll().get(0);
        assertThat(postSaved.getId()).isNotEqualTo(null);
        assertThat(postSaved.getDescription()).isEqualTo(heading);
        assertThat(postSaved.getCategories().isEmpty()).isTrue();
    }

    @Test
    void saveWithCategory() {
        Country country = new Country();
        country.setName("Ukraine");
        Region region = new Region();
        country.setRegions(List.of(region));
        final String name = "Rivne";
        region.setName(name);
        final Post post = new Post();
        final String heading = "first post some heading";
        post.setDescription(heading);
        post.setDate(LocalDateTime.now());
        final PostPhoto photo = new PostPhoto();
        final String path = "some/linux/path/like/normal/operation/system";
        photo.setRelativePathToPhoto(path);
        post.setPostPhoto(photo);
        final String link = "localH0sT";
        post.setLink(link);
        region.setPosts(List.of(post));

        PostCategory postCategory = new PostCategory();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setNumber(BigDecimal.valueOf(9.f));
        categoryEntity.setName("food");
        categoryEntity.setUnits("kg");
        postCategory.setCategoryEntity(categoryEntity);
        post.setCategories(List.of(postCategory));
        countryRepository.save(country);

        var postSaved = repository.findAll().get(0);
        assertThat(postSaved.getCategories()).isNotNull();
        assertThat(postSaved.getCategories().size()).isEqualTo(1);
        PostCategory postCategorySavedInPostCollection = postSaved.getCategories().get(0);
        assertThat(postCategorySavedInPostCollection).isEqualTo(postCategory);

        CategoryEntity categoryEntity1 = categoryRepository.findAll()
                .stream().filter(x -> categoryEntity.getName().equals(x.getName()))
                .findFirst().orElseThrow(IllegalStateException::new);
        assertThat(categoryEntity).isEqualTo(categoryEntity1);

        var postCategories = postCategoryRepository.findAll();
        assertThat(postCategories.isEmpty()).isFalse();
        assertThat(postCategories.get(0)).isEqualTo(postCategory);

        var regionCategories = regionCategoryRepository.findAll();
        assertThat(regionCategories.isEmpty()).isTrue();
    }

    @Test
    void checkPhotoSaving() {
        Country country = new Country();
        country.setName("Ukraine");
        Region region = new Region();
        country.setRegions(List.of(region));
        final String name = "Rivne";
        region.setName(name);

        final Post post = new Post();
        final String heading = "first post some heading";
        post.setDescription(heading);
        post.setDate(LocalDateTime.now());
        final PostPhoto photo = new PostPhoto();
        final String path = "some/linux/path/like/normal/operation/system";
        photo.setRelativePathToPhoto(path);
        post.setPostPhoto(photo);
        final String link = "localH0sT";
        post.setLink(link);
        region.setPosts(List.of(post));
        countryRepository.save(country);

        List<PostPhoto> all = postPhotoRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
        PostPhoto savedPhoto = all.get(0);
        assertThat(savedPhoto.getId()).isNotNull();
        assertThat(savedPhoto).isEqualTo(photo);
    }
}
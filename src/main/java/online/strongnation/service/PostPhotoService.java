package online.strongnation.service;

import online.strongnation.model.entity.PostPhoto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostPhotoService {
    Long uploadPhotoByPostId(Long id, MultipartFile file); //post's id

    Resource downloadPhotoByPostId(Long id); //post's id

    String deletePhotoByPostId(Long id);

    String deletePhotoByPostPhoto(PostPhoto postPhoto);

    List<String> deletePhotoCountryId(Long id);

    List<String> deletePhotoByRegionId(Long id);

    List<String> deleteAll();
}

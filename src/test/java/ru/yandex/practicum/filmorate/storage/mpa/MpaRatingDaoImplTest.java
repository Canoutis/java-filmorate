package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRatingDaoImplTest {

    private final MpaRatingDaoImpl mpaRatingDaoImpl;

    @Test
    public void testGetMpaRatingById() {
        MpaRating mpaG = mpaRatingDaoImpl.getMpaRatingById(1);
        Assertions.assertEquals("G", mpaG.getName());
    }

    @Test
    public void testFindAllMpaRatings() {
        List<MpaRating> mpaRatings = mpaRatingDaoImpl.findAll();
        Assertions.assertEquals(5, mpaRatings.size());
    }

}

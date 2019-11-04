import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sound.sampled.Line;

@RunWith(MockitoJUnitRunner.class)
public class InfoStoreTest {
    @Mock
    InfoStore info;
    @Test
    public void getIDTest(){
        info = new InfoStore("123","david","1233");
        Assert.assertEquals("123",info.getId());
    }
    @Test
    public void getLoginTest(){
        info = new InfoStore("123","david","1233");
        Assert.assertEquals("david",info.getLogin());
    }
    @Test
    public void getPasswordTest(){
        info = new InfoStore("123","david","1233");
        Assert.assertEquals("1233",info.getPassword());
    }
}

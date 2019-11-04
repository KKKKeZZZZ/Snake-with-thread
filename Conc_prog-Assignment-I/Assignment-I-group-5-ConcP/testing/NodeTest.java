import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {
    @Mock
    Node node;
    @Test
    public void getXTest(){
        node = new Node(1,1);
        Assert.assertEquals(1,node.getX());
    }
    @Test
    public void getYTest(){
        node = new Node(new Node(1,1));
        Assert.assertEquals(1,node.getY());
    }
    @Test
    public void equalTest(){
        node = new Node(1,1);
        Node compare = new Node(new Node(1,1));
        Assert.assertTrue(node.equals(compare));

    }
}

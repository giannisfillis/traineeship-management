package myy803.traineeship_management.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import myy803.traineeship_management.domainmodel.pos_search_strategies.CompositeSearch;
import myy803.traineeship_management.domainmodel.pos_search_strategies.PositionSearchFactory;
import myy803.traineeship_management.domainmodel.pos_search_strategies.PositionSearchStrategy;
import myy803.traineeship_management.domainmodel.pos_search_strategies.SearchBasedOnInterests;
import myy803.traineeship_management.domainmodel.pos_search_strategies.SearchBasedOnLocation;

@ExtendWith(MockitoExtension.class)
public class PositionSearchFactoryTest {

    @Mock 
    private SearchBasedOnInterests searchBasedOnInterests;
    
    @Mock
    private SearchBasedOnLocation searchBasedOnLocation;
    
    @Mock
    private CompositeSearch compositeSearch;

    @InjectMocks
    private PositionSearchFactory positionSearchFactory;

    @Test
    public void testCreateLocationStrategy() {
    	PositionSearchStrategy strategy = positionSearchFactory.create("Location");
        assertSame(searchBasedOnLocation, strategy);
    }

    @Test
    public void testCreateInterestsStrategy() {
    	PositionSearchStrategy strategy = positionSearchFactory.create("Interests");
        assertSame(searchBasedOnInterests, strategy);
    }

    @Test
    public void testCreateCompositeStrategyOnAnythingElse() {
    	PositionSearchStrategy strategy1 = positionSearchFactory.create("Anything Else");
    	PositionSearchStrategy strategy2 = positionSearchFactory.create("Composite");
    	PositionSearchStrategy strategy3 = positionSearchFactory.create("");
    	
    	//returns composite strategy on any other case
        assertSame(compositeSearch, strategy1);
        assertSame(compositeSearch, strategy2);
        assertSame(compositeSearch, strategy3);
        
    }
}
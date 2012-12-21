package com.xtreme.doctorapp.domain;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = Specialist.class)
public class SpecialistIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private SpecialistDataOnDemand dod;

	@Test
    public void testCountSpecialists() {
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", dod.getRandomSpecialist());
        long count = Specialist.countSpecialists();
        Assert.assertTrue("Counter for 'Specialist' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindSpecialist() {
        Specialist obj = dod.getRandomSpecialist();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to provide an identifier", id);
        obj = Specialist.findSpecialist(id);
        Assert.assertNotNull("Find method for 'Specialist' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Specialist' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllSpecialists() {
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", dod.getRandomSpecialist());
        long count = Specialist.countSpecialists();
        Assert.assertTrue("Too expensive to perform a find all test for 'Specialist', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Specialist> result = Specialist.findAllSpecialists();
        Assert.assertNotNull("Find all method for 'Specialist' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Specialist' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindSpecialistEntries() {
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", dod.getRandomSpecialist());
        long count = Specialist.countSpecialists();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Specialist> result = Specialist.findSpecialistEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Specialist' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Specialist' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Specialist obj = dod.getRandomSpecialist();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to provide an identifier", id);
        obj = Specialist.findSpecialist(id);
        Assert.assertNotNull("Find method for 'Specialist' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifySpecialist(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Specialist' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        Specialist obj = dod.getRandomSpecialist();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to provide an identifier", id);
        obj = Specialist.findSpecialist(id);
        boolean modified =  dod.modifySpecialist(obj);
        Integer currentVersion = obj.getVersion();
        Specialist merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Specialist' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", dod.getRandomSpecialist());
        Specialist obj = dod.getNewTransientSpecialist(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Specialist' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Specialist' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'Specialist' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        Specialist obj = dod.getRandomSpecialist();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Specialist' failed to provide an identifier", id);
        obj = Specialist.findSpecialist(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Specialist' with identifier '" + id + "'", Specialist.findSpecialist(id));
    }
}

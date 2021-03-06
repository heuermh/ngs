/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.ngs.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.tools.FilterConsensus.cigarToEditList;
import static org.nmdp.ngs.tools.FilterConsensus.readBedFile;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import java.nio.charset.Charset;

import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.biojava.bio.symbol.Edit;

import org.nmdp.ngs.feature.Allele;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;

/**
 * Unit test for FilterConsensus.
 */
public final class FilterConsensusTest {
    private File bamFile;
    private File bedFile;
    private File outputFile;
    private String gene;
    private boolean cdna;
    private boolean removeGaps;
    private double minimumBreadth;
    private int expectedPloidy;

    @Before
    public void setUp() throws Exception {
        bamFile = File.createTempFile("filterConsensusTest", ".bam");
        bedFile = File.createTempFile("filterConsensusTest", ".bed");
        outputFile = File.createTempFile("filterConsensusTest", ".fa");
        gene = "HLA-A";
        cdna = true;
        removeGaps = true;
        minimumBreadth = FilterConsensus.DEFAULT_MINIMUM_BREADTH;
        expectedPloidy = FilterConsensus.DEFAULT_EXPECTED_PLOIDY;
    }

    @After
    public void tearDown() throws Exception {
        bamFile.delete();
        bedFile.delete();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new FilterConsensus(bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy));
    }

    @Test
    public void testEmptyReadBedFile() throws Exception {
        Map<Integer, Allele> exons = readBedFile(bedFile);
        assertTrue(exons.isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadGenomicFileMissingName() throws Exception {
        copyResource("missing-name.bed", bedFile);
        readBedFile(bedFile);
    }

    @Test(expected=IOException.class)
    public void testReadGenomicFileInvalidName() throws Exception {
        copyResource("invalid-name.bed", bedFile);
        readBedFile(bedFile);
    }

    @Test
    public void testReadBedFile() throws Exception {
        copyResource("hla-a.bed", bedFile);
        Map<Integer, Allele> exons = readBedFile(bedFile);
        assertFalse(exons.isEmpty());
        assertTrue(exons.containsKey(2));
        assertTrue(exons.containsKey(3));
    }

    @Test
    public void testEmptyCigarToEditList() throws Exception {
        List<Edit> edits = cigarToEditList(new SAMRecord(new SAMFileHeader()));
        assertTrue(edits.isEmpty());
    }

    @Test
    public void testCall() throws Exception {
        copyResource("hla-a.bam", bamFile);
        copyResource("hla-a.bed", bedFile);
        new FilterConsensus(bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy).call();
    }
    
    @Test
    public void testCallPloidyDefault() throws Exception {
        Files.write(Resources.toByteArray(getClass().getResource("testfile2.bam")), bamFile);
        Files.write(Resources.toByteArray(getClass().getResource("hla-a.bed")), bedFile);

        new FilterConsensus(bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy).call();
        assertEquals(4, countLines(outputFile));
    }

    @Test
    public void testCallPloidyThree() throws Exception {
        Files.write(Resources.toByteArray(getClass().getResource("testfile2.bam")), bamFile);
        Files.write(Resources.toByteArray(getClass().getResource("hla-a.bed")), bedFile);

        new FilterConsensus(bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, 3).call();
        assertEquals(6, countLines(outputFile));
    }
    
    @Test
    public void testCallKirExons() throws Exception {
        Files.write(Resources.toByteArray(getClass().getResource("2DL1_0020101.bwa.sorted.bam")), bamFile);
        Files.write(Resources.toByteArray(getClass().getResource("kir-2dl1.exons.bed")), bedFile);

        cdna = false;
        removeGaps = false;
        new FilterConsensus(bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy).call();
        assertEquals(16, countLines(outputFile));
    }

    private static int countLines(final File file) throws Exception {
        return Files.readLines(file, Charset.forName("UTF-8")).size();
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(FilterConsensusTest.class.getResource(name)), file);
    }
}

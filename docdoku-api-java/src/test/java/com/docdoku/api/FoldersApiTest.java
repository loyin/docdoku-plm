/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.api;

import com.docdoku.api.client.ApiException;
import com.docdoku.api.models.FolderDTO;
import com.docdoku.api.services.FoldersApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class FoldersApiTest {

    @Test
    public void getRootFoldersTest() throws ApiException {
        FoldersApi foldersApi = new FoldersApi(TestConfig.REGULAR_USER_CLIENT);
        List<FolderDTO> rootFolders = foldersApi.getRootFolders(TestConfig.WORKSPACE);
        Assert.assertNotNull(rootFolders);
    }

    @Test
    public void createRootFoldersTest() throws ApiException {
        String folderName = "Folder-" + TestUtils.randomString();
        FolderDTO folder = new FolderDTO();
        folder.setName(folderName);
        FoldersApi foldersApi = new FoldersApi(TestConfig.REGULAR_USER_CLIENT);
        foldersApi.createSubFolder(TestConfig.WORKSPACE, TestConfig.WORKSPACE, folder);
        List<FolderDTO> rootFolders = foldersApi.getRootFolders(TestConfig.WORKSPACE);
        Assert.assertEquals(rootFolders.stream()
                .filter(folderDTO -> folderName.equals(folderDTO.getName()))
                .count(), 1);
    }

    @Test
    public void nonExistingFolderTest() throws ApiException {
        String nonExistingFolderName = "SomethingNotExisting";
        String folderCompletePath = TestConfig.WORKSPACE + ":" + nonExistingFolderName;
        FoldersApi foldersApi = new FoldersApi(TestConfig.REGULAR_USER_CLIENT);
        try {
            foldersApi.getSubFolders(TestConfig.WORKSPACE, folderCompletePath);
        } catch (ApiException e) {
            Assert.assertEquals(404, e.getCode());
        }
    }


    @Test
    public void existingFolderTest() throws ApiException {

        String folderName = "Folder-" + TestUtils.randomString();
        FolderDTO folder = new FolderDTO();
        folder.setName(folderName);
        FoldersApi foldersApi = new FoldersApi(TestConfig.REGULAR_USER_CLIENT);
        foldersApi.createSubFolder(TestConfig.WORKSPACE, TestConfig.WORKSPACE, folder);

        String subFolderName = "SubFolder-" + TestUtils.randomString();
        FolderDTO subFolder = new FolderDTO();
        subFolder.setName(subFolderName);
        FolderDTO createdSubFolder = foldersApi.createSubFolder(TestConfig.WORKSPACE, TestConfig.WORKSPACE + ":" + folderName, subFolder);
        List<FolderDTO> subFolders = foldersApi.getSubFolders(TestConfig.WORKSPACE, TestConfig.WORKSPACE + ":" + folderName);

        Assert.assertEquals(1,subFolders.size());
        Assert.assertEquals(createdSubFolder.getName(),subFolders.get(0).getName());

    }
}

/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2014 DocDoku SARL
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
package com.docdoku.core.configuration;

import com.docdoku.core.common.Workspace;
import com.docdoku.core.document.DocumentIteration;
import com.docdoku.core.document.DocumentMasterKey;
import com.docdoku.core.document.Folder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Baseline refers to a specific configuration of document, it could be seen as
 * "snapshots in time" of folders. More concretely, baselines are collections
 * of items (like documents) at a specified iteration.
 *
 * @author Taylor LABEJOF
 * @version 2.0, 25/08/14
 * @since   V2.0
 */
@Table(name="DOCUMENTBASELINE")
@Entity
public class DocumentBaseline implements Serializable {
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "WORKSPACE_ID", referencedColumnName = "ID")
    })
    private Workspace workspace;

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private DocumentCollection documentCollection =new DocumentCollection();

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private FolderCollection folderCollection =new FolderCollection();

    public DocumentBaseline() {
    }

    public DocumentBaseline(Workspace workspace, String name, String description) {
        this.workspace = workspace;
        this.name = name;
        this.description = description;
        this.creationDate = new Date();
    }

    public Map<String, BaselinedFolder> getBaselinedFolders() {
        return folderCollection.getBaselinedFolders();
    }
    public void removeAllBaselinedFolders() {
        folderCollection.removeAllBaselinedFolders();
    }

    public void addBaselinedFolder(Folder folder){
        folderCollection.addBaselinedFolder(folder);
    }
    public void addBaselinedFolder(BaselinedFolder baselinedFolder){
        folderCollection.addBaselinedFolder(baselinedFolder);
    }
    public boolean hasBasedLinedFolder(String completePath){
        return folderCollection.hasBaselinedFolder(completePath);
    }
    public BaselinedFolder getBaselinedFolder(String completePath){
        return folderCollection.getBaselinedFolder(completePath);
    }

    public Map<BaselinedDocumentKey, BaselinedDocument> getBaselinedDocuments() {
        return documentCollection.getBaselinedDocuments();
    }
    public void removeAllBaselinedDocuments() {
        documentCollection.removeAllBaselinedDocuments();
    }

    public BaselinedDocument addBaselinedDocument(DocumentIteration targetDocument){
        return documentCollection.addBaselinedDocument(targetDocument);
    }
    public boolean hasBasedLinedDocument(DocumentMasterKey documentMasterKey){
        return documentCollection.hasBaselinedDocument(documentMasterKey);
    }
    public BaselinedDocument getBaselinedDocument(BaselinedDocumentKey baselinedDocumentKey){
        return documentCollection.getBaselinedDocument(baselinedDocumentKey);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return (creationDate!=null) ? (Date) creationDate.clone(): null;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = (Date) creationDate.clone();
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentCollection getDocumentCollection() {
        return documentCollection;
    }
    public FolderCollection getFolderCollection() {
        return folderCollection;
    }

    public Workspace getWorkspace() {
        return workspace;
    }
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentBaseline)) {
            return false;
        }

        DocumentBaseline baseline = (DocumentBaseline) o;
        return id == baseline.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
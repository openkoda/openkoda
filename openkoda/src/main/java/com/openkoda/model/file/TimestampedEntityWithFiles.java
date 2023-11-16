/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

 /*
 TODO: rewrite
  */
@MappedSuperclass
public abstract class TimestampedEntityWithFiles extends TimestampedEntity implements EntityWithFiles {

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(
            name="file_reference",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseJoinColumns =  @JoinColumn(name = "file_id"),
            joinColumns = @JoinColumn(name = "organization_related_entity_id", insertable = false, updatable = false)
    )
    @JsonIgnore
    @OrderColumn(name="sequence")
    protected List<File> files;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
    @CollectionTable(name = "file_reference", joinColumns = @JoinColumn(name = "organization_related_entity_id"), foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Column(name="file_id")
    @OrderColumn(name="sequence")
    protected List<Long> filesId = new ArrayList<>();

    public void setFilesId(List<Long> filesId) {
        this.filesId = filesId;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Long> getFilesId() {
        return filesId;
    }

    public boolean hasFiles() {
        return files != null && files.size() > 0;
    }

}
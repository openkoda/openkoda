package com.openkoda.uicomponent;

import com.openkoda.model.component.ServerJs;
import com.openkoda.repository.ServerJsRepository;
import com.openkoda.repository.specifications.ServerJsSpecification;
import jakarta.inject.Inject;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.FileSystem;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.openkoda.repository.specifications.ServerJsSpecification.getByName;


@Component
public class FileSystemImpl implements FileSystem {
    @Inject
    ServerJsRepository serverJsRepository;
    @Override
    public Path parsePath(URI uri) {
        return null;
    }

    @Override
    public Path parsePath(String path) {
        return Paths.get(path);
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {

    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

    }

    @Override
    public void delete(Path path) throws IOException {

    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
       Optional<ServerJs> optional = serverJsRepository.findOne(getByName(path.toString()));
       if(optional.isPresent()) {
           return new SeekableInMemoryByteChannel(optional.get().getCode().getBytes());
       }
        throw new RuntimeException("Can't get code for path " + path);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;
    }

    @Override
    public Path toAbsolutePath(Path path) {
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        return path;
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;
    }
}

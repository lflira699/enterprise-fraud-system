package com.efs.modules.transaction.validator;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TransactionAttachmentValueValidator {

    private static final Pattern SHA_256_HEX =
            Pattern.compile("[A-Fa-f0-9]{64}");

    public void validate(
            TransactionAttachmentRequest request) {

        Long fileSize =
                request.getFileSize();

        if (fileSize != null && fileSize < 0) {
            throw new IllegalArgumentException(
                    "File size cannot be negative: "
                            + fileSize
            );
        }

        String checksumSha256 =
                request.getChecksumSha256();

        if (
                checksumSha256 != null
                        && !SHA_256_HEX
                                .matcher(checksumSha256)
                                .matches()
        ) {
            throw new IllegalArgumentException(
                    "Invalid SHA-256 checksum"
            );
        }
    }
}

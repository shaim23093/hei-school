package school.hei.admin.file.hash;

import school.hei.admin.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}

package org.vsanyc.sandbox.couchbase.entities;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Area {

    private String id;

    private String name;

    private List<Area> areas = new ArrayList<>();
}

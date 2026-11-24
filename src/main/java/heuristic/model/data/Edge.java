package heuristic.model.data;

import lombok.Value;

/// The tour edge between two cities, with the ids in tour order.
@Value
public class Edge {

    int firstCityId;
    int secondCityId;
}

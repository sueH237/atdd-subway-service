package nextstep.subway.station.domain;

import io.jsonwebtoken.lang.Assert;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.BaseEntity;
import nextstep.subway.common.domain.Name;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    protected Station() {
    }

    private Station(Name name) {
        Assert.notNull(name, "'name' must not be null");
        this.name = name;
    }

    public static Station from(Name name) {
        return new Station(name);
    }

    public Long id() {
        return id;
    }

    public Name name() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id) &&
            Objects.equals(name, station.name);
    }
}

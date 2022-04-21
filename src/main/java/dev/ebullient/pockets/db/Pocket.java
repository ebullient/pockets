package dev.ebullient.pockets.db;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * A pocket (or backpack, or haversack, or purse, or ... )
 */
@Entity(name = EntityConstants.POCKET_ENTITY)
@Table(name = EntityConstants.POCKET_TABLE)
public class Pocket extends PanacheEntity {

    @Size(min = 1, max = 50)
    public String name;

    @NotNull // identifier: name-as-slug
    public String slug;

    @NotNull
    public String pocketRef;

    public Double max_weight; // in lbs
    public Double max_volume; // in cubic ft, might be null

    public String notes;

    @NotNull
    public Double weight; // weight of the pocket itself

    @NotNull // extradimensional always have the same carry weight
    public boolean extradimensional = false;

    /** Many items in this pocket */
    @OneToMany(mappedBy = EntityConstants.POCKET_TABLE, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Item> items;

    /**
     * Add an item to the pocket
     *
     * @see Item#addToPocket(Pocket)
     */
    public boolean addItem(Item item) {
        if (item.pocket == null && items.add(item)) {
            item.pocket = this;
            return true;
        }
        return false;
    }

    /**
     * Remove an item from the pocket
     *
     * @see Item#removeFromPocket(Pocket)
     */
    public boolean removeItem(Item item) {
        if (items.remove(item)) {
            item.pocket = null;
            return true;
        }
        return false;
    }

    @Override
    public void persist() {
        slug = Mapper.slugify(name);
        super.persist();
    }

    @Override
    public void persistAndFlush() {
        slug = Mapper.slugify(name);
        super.persistAndFlush();
    }

    /**
     * Find pocket by slugified name
     *
     * @param name -- will be slugified
     * @return List of pockets that match the slugified name
     */
    public static List<Pocket> findByName(String name) {
        final String query = Mapper.slugify(name);
        List<Pocket> allPockets = Pocket.listAll();
        return allPockets.stream()
                .filter(p -> p.slug.startsWith(query))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Pocket [extradimensional=" + extradimensional + ", max_volume=" + max_volume + ", max_weight="
                + max_weight + ", name=" + name + ", pocketRef=" + pocketRef + ", slug=" + slug + ", weight=" + weight
                + "]";
    }
}

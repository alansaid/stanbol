package org.apache.stanbol.ontologymanager.store.adapter.util;

import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.ontologymanager.store.adapter.SimpleContentItem;

/*
 * In this scope this class is used to store contentType-content-storeid
 */
public final class Triple<K,V,L> {

    private static final String ELEMENT_SEPARATOR = ",";

    private K entry1;

    private V entry2;

    private L entry3;

    public Triple(final K entry1, final V entry2, final L entry3) {
        this.entry1 = entry1;
        this.entry2 = entry2;
        this.entry3 = entry3;

    }

    public static Triple<String,String,String> createTriple(final String string) {
        String[] field = string.split(ELEMENT_SEPARATOR);
        String entry1 = field[0];
        String entry2 = field[1];
        String entry3 = field[2];
        return new Triple<String,String,String>(entry1, entry2, entry3);
    }

    public K getEntry1() {
        return entry1;
    }

    public V getEntry2() {
        return entry2;
    }

    public L getEntry3() {
        return entry3;
    }

    public void setEntry1(final K entry1) {
        this.entry1 = entry1;
    }

    public void setEntry2(final V entry2) {
        this.entry2 = entry2;
    }

    public void setEntry3(final L entry3) {
        this.entry3 = entry3;
    }

    @Override
    public String toString() {
        // Return entries in comma separated from
        StringBuilder sb = new StringBuilder();
        sb.append(entry1).append(ELEMENT_SEPARATOR);
        sb.append(entry2).append(ELEMENT_SEPARATOR);
        sb.append(entry3);
        return sb.toString();
    }

    public ContentItem toContentItem() {
        return new SimpleContentItem((String) entry1, null, (String) entry3, (byte[]) entry2);
    }

}

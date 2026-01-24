package com.tinybrowser.css;

public class Declaration {
    private String property;
    private String value;

    public Declaration() {
    }

    public Declaration(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return property + ": " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Declaration that = (Declaration) obj;
        return property.equals(that.property) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return 31 * property.hashCode() + value.hashCode();
    }
}

# Bean Property Controller

## Caveat Emptor

Hello! You are looking at an imported repository from [Google Code](https://code.google.com/p/bean-property-controller/) of a project I did almost five years ago (that is, November of 2009). BPC itself is a reflection based bean property controller I put together a long time ago and while it is for the most part not used (to my knowledge), it's still a pretty good rundown of how Java's reflection API works and what one can do with it. All the entries below are the original wiki pages pasted together in no specific order and besides markup fixes and some other style prettifying edits they are what I very originally typed. I might think of some things in different way now, but that is a whole separate topic.

## Purpose
BeanPropertyController's (BPC from here forward) purpose is to enable the user to write generic code which can take in beans and perform freeform actions based on the bean's properties. BPC also allows the user to use beans as pass-through objects for collecting and transferring data automatically.

## Origins of BPC
As an idea utility class such as BPC isn't a new one, in fact it's a piece of code everyone seems to reinvent every now and then. I'm most certain there is just as many implementations as there are developers out there and while some of those implementations are already freely available such as [Apache Commons BeanUtils](http://commons.apache.org/beanutils/index.html), I did feel the need to create an utility which does exactly this one thing and this one thing only.

Comparing to Apache Commons BeanUtils?, BPC is similar to either its PropertyUtils? or perhaps DynaBean? but focuses on easy usage and self-documenting code. That is also partly the reason why BPC itself doesn't have that many methods which are based on common conveniences but instead uses a sort of practical vocabulary for some actions, the main goal is to signify that you are not trying to use some other object transparently in a sort of stealthy way but you are indeed *controlling* the underlying object.

## Design

### Before we delve into details...

By concious decision BeanPropertyController breaks away from common conventions for various reasons which are discussed in detail below. This is admittedly a bit of a contradiction because BPC's functionality is made entirely possible only by the fact that Java is heavily based on conventions, especially the Java beans themselves. BPC also breaks away from all kinds of Java bean conventions quite fast if you look closer into its API but as with other design details, keep reading to find out more.

### Accessor/mutator method naming

If you step back a bit and consider the famous `getter/setter` method pair of beans as what they represent instead of something you just autogenerate in Eclipse, you should know that they're really called accessors and mutators for object's specific property. This is the number one reason why instead of `get(String propertyName)` BPC provides `access(String propertyName)` and similarly why `set(String propertyName, Object value)` is replaced with `mutate(String propertyName, Object value)`. The idea is to signify that you are controlling another object and BPC itself isn't a bean but a class with specific functionality and thus I want to discourage users from using it as a bean but an utility instead for doing this specific task.

### BeanPropertyController object construction

Instead of providing a constructor a bunch of factory methods are provided instead, namely the `of()` methods. This is partly because BPC needs certain set of values whenever it's constructed and while it could've been done with constructors, `BeanPropertyController.of(someObject)` is easier to understand than `new BeanPropertyController(someObject)`. Note that this sort of naming convention for construction isn't unique, [http://java.sun.com/j2se/1.5.0/docs/api/java/util/EnumSet.html](Java's EnumSet) uses the same paradigm too and so does [http://code.google.com/p/google-collections/](Google Collections).

### Lazy loading

Although in automated environment it's not obvious, BPC is a lazy-loading utility. That is, every single extracted property is extracted only when they're actually needed. Understandably `getPropertyNames()` completely nullifies this feature since it scans the entire controlled object but the goal here was to provide a slightly more memory efficient extraction tool for situations where you know the name of the properties you're interested of beforehand. Each extracted property is of course cached so the extraction is done only once.

### Everything in one package

The reason everything is in one package is that in Java there isn't a visibility modifier for "current package and it's subpackages" and I felt the need to limit the available API quite heavily. The reason is that it's much easier to expose new methods than deprecate old ones and also the internal API isn't in a stable state yet although the functionality is there.

### Undetachable BeanProperty

Some other similar implementations have detachable BeanProperties, that is you can actually acquire a list of BeanProperty classes from the controller object and use those to control individual property. While this is sort of nice in some cases, the issue here is that such functionality

 - leaks abstraction and exposes internals of the actual utility and
 - makes reusability annoying to do - for example BPC's `recycle()` couldn't be done easily with detachable properties without at least losing performance

### Breaking conventions with bean handling

While some (possibly outdated) documentation exists in the actual BeanPropertyController class for these, here's an explanation for the various options you can use when constructing BPC.

#### ExtractionDepth

Extraction depth is the second biggest convention breaker in BPC, it basically allows you to choose how deeply the object is scanned for detecting properties.
 - `METHODS` (which is the default) scans only methods which fall into common conventions (`[is/get]X() / setX(T x)`)
 - `FIELDS` includes actual fields to the scanning meaning that a mutable property may be formed from `getX()` and field with matching name that isn't a declared field
 - `QUESTIMATE` is the "everything goes" option in which otherwise inaccessible declared fields are also included to the scan

#### InstantiationPolicy

Biggest. Convention breaker. Ever. Currently has two options:
 - `NO_ARGS`, default behaviour, searches for the constructor with 0 arguments and uses that
 - `NICE`, uses nice values (zeros, `false`, `""` and so on) to instantiate the object with its shortest available constructor. Doesn't currently handle beans within beans, that may change in the future

Also in future versions the following may be available too:
 - `JVM_FORCED_INSTANTIATION` Force creation of instance using JVM internals even if public constructor doesn't exist. I already know how to do this with Sun's JVM and if this is specifically requested, I will add it.
 - `PROXY_INSTANCE` for creating a proxy which mimics the given class, think creating ad hoc beans from interfaces.

#### stepping

Consider beans within beans, such as `Person` who has a `House` that has an `Address`. To get to the house number from person you'd traditionally write `person.getHouse().getAddress().getHouseNumber()`, with BPC this same is done with property String `house.address.houseNumber`. Since this is part of a recursive scan into the object, a way to limit this has been provided.

Default stepping is -1 which means infinite stepping, otherwise the number specifies how many steps into the bean's internal structure can be taken. In the example above stepping of 1 would limit the `house` to address and 2 would limit the access to `address`.

## Usage Examples

All the examples below use the following bean (unless otherwise stated):
```java
public class TraditionalBean {
    
    private String name;
    private int age;
    private double accountBalance;
    
    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public int hashCode() { /* hashCode generation code */ return hashCode; }
    @Override
    public boolean equals(Object obj) { /*equality checking code*/ return equal; }
}
```

### Mutate known field of object
```java
TraditionalBean bean = new TraditionalBean();
BeanPropertyController bpc = BeanPropertyController.of(bean);
bpc.mutate("name", "Jane");
assert bpc.access("name").equals("Jane");
```

### Mutate known field of class

```java
BeanPropertyController bpc = BeanPropertyController.of(TraditionalBean.class);
bpc.mutate("name", "Jane");
assert bpc.access("name").equals("Jane");
```

### Iterate through all properties of given bean

```java
BeanPropertyController bpc = BeanPropertyController.of(new TraditionalBean());

for (String propertyName : bpc.getPropertyNames()) {
	// do something with the property
}
```

### Use BPC as an object factory

```java
List<TraditionalBean> beans = new ArrayList<TraditionalBean>();
BeanPropertyController bpc = BeanPropertyController.of(TraditionalBean.class);
for (int i=0; i<10; i++) {
	bpc.mutate("age", i);
	beans.add(bpc.getObject(TraditionalBean.class));
	bpc.recycle();
}
assert beans.size() == 10;

for (int i=0; i<beans.size(); i++) {
	assert beans.get(i).getAge() == i;
}
```

### Check various aspects of the properties through convenience methods

```java
BeanPropertyController bpc = BeanPropertyController.of(TraditionalBean.class);
assert !bpc.isArray("age");
assert bpc.isReadOnly("name"); // means there's no mutator available, .mutate() has no effect
assert bpc.typeOf("accountBalance").equals(double.class);
```

### Get value from bean within a bean

```java
BeanPropertyController bpc = BeanPropertyController.of(NestedBean.class);
bpc.mutate("nested.value", "I'm a property inside a bean which is inside the NestedBean!");
```

## License

As of 15 November 2014 this utility is licensed under [MIT](http://en.wikipedia.org/wiki/MIT_License). Previous version(s) available at [Google Code](https://code.google.com/p/bean-property-controller/) is licensed under ASF 2.0
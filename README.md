#University of Alacalá Indoor Positioning APP

### Overview 

This is a functional Android application built using Mapbox.

### Disclaimer

1. It contains [Mapbox SDK](https://www.mapbox.com/) and it has its own support Mapping applications.

2. This application is just another way to build an Indoor Application using different libraries to build application on the fly. Be aware this is not meant to be a blueprint, the correct way, even worse, the best way to build an IPS. On the contrary, it was built purely for fun and curiosity. Surely, there are plenty different ways to build a similar project using OSM, Google Maps, ARGIS, among others. However, I hope this can be useful offering some guidance for those trying to build Indoor APPs.
 
### Requirements for running it locally

* **Virtualized environment**

	This project has been using the following versions of the tools aforementioned:

	- Android Studio 4.0.1

* **On your own**

	Currently, the project has the following dependencies:

	- CMake 3.7.2
	- GCC-6 and G++-6
	- Google's C++ test framework
	- Poco 1.8.1
	- MySQL

### Running the project

After having [Vagrant](https://www.vagrantup.com/docs/installation), [VirtualBox](https://www.virtualbox.org) and [Puppet](https://docs.puppet.com/puppet/3.8/install_debian_ubuntu.html) installed you can run the web service locally through a few **Ant Tasks** at the root directory of the project as follows:

###### Managing application environment
  
For making the environment available locally:
  
```bash
ant -propertyfile dev.properties setup_environment
```

or, using **Vagrant** directly applying this command on the root directory of the 	project: 

```bash
vagrant up
```

For destroying the environment with everything:

```bash
ant -propertyfile dev.properties destroy_environment
```

or, using **Vagrant** directly applying this command on the root directory of the 	project: 

```bash
vagrant destroy
```

###### Managing application database 

For making the database available:

```bash
ant -propertyfile dev.properties create_schema
ant -propertyfile dev.properties init_schema
```

For destroying the database:

```bash
ant -propertyfile dev.properties drop_schema
```

###### Managing the development process
  
```bash
ant -propertyfile dev.properties create_build_directory
ant -propertyfile dev.properties clean_build_directory
ant -propertyfile dev.properties build_project
ant -propertyfile dev.properties compile_project
ant -propertyfile dev.properties run_tests
```

## Usage

After having run the project you will be able to make requests to the API. 
For doing this, use a **Terminal** tool like [cUrl](https://curl.haxx.se) or any **REST Client Tool** like [ARC](https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo) or [Postman](https://www.getpostman.com).   
You reach the service at the IP address `192.168.1.100:9090`.

### Running Requests and Responses

For seeing a complete list of API resources and how to make requests and responses consult the API [documentation](docs/api/ReferenceGuide.md).

### Standards and Style

This project do not follow any specific **Coding Style Guidelines or Standard**. But, it has been influenced by [Google C++ Style Guide](https://google.github.io/styleguide/cppguide.html), 
[PPP Style Guide](http://www.stroustrup.com/Programming/PPP-style-rev3.pdf) and [Applied Informatics C++ Coding Style Guide](https://www.appinf.com/download/CppCodingStyleGuide.pdf) itself.  

### Useful links

* [JSON API](http://jsonapi.org)
* [Doxygen](http://www.stack.nl/~dimitri/doxygen/manual/index.html)
* [Google Test](https://github.com/google/googletest/blob/master/googletest/docs/Primer.md)


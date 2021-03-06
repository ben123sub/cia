module com.hack23.cia.service.external.esv {
	exports com.hack23.cia.service.external.esv.api;
	exports com.hack23.cia.service.external.esv.impl;

	requires org.slf4j;
	requires java.xml.bind;
	requires spring.context;
	requires spring.beans;
	requires poi;

	requires org.apache.commons.lang3;

	requires com.hack23.cia.service.external.common;
}
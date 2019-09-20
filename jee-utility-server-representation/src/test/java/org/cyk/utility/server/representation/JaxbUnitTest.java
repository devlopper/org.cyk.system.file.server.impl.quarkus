package org.cyk.utility.server.representation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.json.bind.JsonbBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.cyk.utility.server.representation.entities.NodeDto;
import org.cyk.utility.server.representation.entities.NodeDtoCollection;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

public class JaxbUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void one_marshal() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(NodeDto.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter writer = new StringWriter();
			NodeDto nodeDto = new NodeDto().setIdentifier("1").setCode("c").setName("n");
			nodeDto.addParents(new NodeDto().setIdentifier("p01").setCode("pc01").setName("pn01"));
			nodeDto.addChildren(new NodeDto().setIdentifier("c01").setCode("cc01").setName("cn01"));
			marshaller.marshal(nodeDto, writer);
			String string = writer.toString();
			assertThat(string)
			.contains("<nodeDto>")
			.contains("<identifier>1</identifier>").contains("<code>c</code>").contains("<name>n</name>")
			.contains("<parents>").contains("</parents>")
			.contains("<children>").contains("</children>")
			.contains("</nodeDto>");
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@Test
	public void nodeDtoCollection_to_json() {
		NodeDtoCollection nodeDtoCollection01 = new NodeDtoCollection();
		nodeDtoCollection01.add(new NodeDto().setIdentifier("1").setCode("c").setName("n"));
		String string = JsonbBuilder.create().toJson(nodeDtoCollection01);
		NodeDtoCollection nodeDtoCollection02 = JsonbBuilder.create().fromJson(string, NodeDtoCollection.class);
		assertThat(nodeDtoCollection02.getCollection()).isNotNull();
		assertThat(nodeDtoCollection02.getCollection()).hasSize(1);
		assertThat(nodeDtoCollection02.getCollection().get(0).getIdentifier()).isEqualTo("1");
		assertThat(nodeDtoCollection02.getCollection().get(0).getCode()).isEqualTo("c");
		assertThat(nodeDtoCollection02.getCollection().get(0).getName()).isEqualTo("n");
	}
	
	@Test
	public void nodeDtoWithParents_to_json() {
		NodeDtoCollection nodeDtoCollection01 = new NodeDtoCollection();
		NodeDto nodeDto01 = new NodeDto().setIdentifier("0").setCode("c0").setName("00");
		nodeDtoCollection01.add(new NodeDto().setIdentifier("1").setCode("c").setName("n"));
		nodeDto01.setParents(nodeDtoCollection01);
		String string = JsonbBuilder.create().toJson(nodeDto01);
		//string = "{\"code\":\"null.0\",\"name\":\"SEx\",\"parents\":{\"collection\":[{}]}}";
		string = "{\"code\":\"null.0\",\"name\":\"Gqe\",\"parents\":{\"collection\":[{\"identifier\":\"1\",\"code\":\"c\",\"name\":\"n\"}]}}";
		NodeDto nodeDto2 = JsonbBuilder.create().fromJson(string, NodeDto.class);
		NodeDtoCollection nodeDtoCollection02 = nodeDto2.getParents();
		assertThat(nodeDtoCollection02.getCollection()).isNotNull();
		assertThat(nodeDtoCollection02.getCollection()).hasSize(1);
		assertThat(nodeDtoCollection02.getCollection().get(0).getIdentifier()).isEqualTo("1");
		assertThat(nodeDtoCollection02.getCollection().get(0).getCode()).isEqualTo("c");
		assertThat(nodeDtoCollection02.getCollection().get(0).getName()).isEqualTo("n");
	}
	
	@Test
	public void array_nodeDtoWithParents_to_json() {
		NodeDtoCollection nodeDtoCollection01 = new NodeDtoCollection();
		NodeDto nodeDto01 = new NodeDto().setIdentifier("0").setCode("c0").setName("00");
		nodeDtoCollection01.add(new NodeDto().setIdentifier("1").setCode("c").setName("n"));
		nodeDto01.setParents(nodeDtoCollection01);
		List<NodeDto> nodeDto01s = List.of(nodeDto01);
		String string = JsonbBuilder.create().toJson(nodeDto01s);
		string = "[{\"code\":\"null.0\",\"name\":\"Gqe\",\"parents\":{\"collection\":[{\"identifier\":\"1\",\"code\":\"c\",\"name\":\"n\"}]}}]";
		List<NodeDto> nodeDto2s = JsonbBuilder.create().fromJson(string, new ArrayList<NodeDto>(){private static final long serialVersionUID = 1L;}.getClass().getGenericSuperclass());
		NodeDtoCollection nodeDtoCollection02 = nodeDto2s.get(0).getParents();
		assertThat(nodeDtoCollection02.getCollection()).isNotNull();
		assertThat(nodeDtoCollection02.getCollection()).hasSize(1);
		assertThat(nodeDtoCollection02.getCollection().get(0).getIdentifier()).isEqualTo("1");
		assertThat(nodeDtoCollection02.getCollection().get(0).getCode()).isEqualTo("c");
		assertThat(nodeDtoCollection02.getCollection().get(0).getName()).isEqualTo("n");
	}
	
	/*
	@Test
	public void one_unmarshal() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(One.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			One one = (One) unmarshaller.unmarshal(getClass().getResourceAsStream("one.xml"));
			Assert.assertEquals("v1", one.getF1());
			Assert.assertEquals("v2", one.getF2());
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	*/
	
}

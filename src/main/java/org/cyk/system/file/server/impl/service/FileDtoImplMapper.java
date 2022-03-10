package org.cyk.system.file.server.impl.service;
import org.cyk.system.file.server.api.service.FileService;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.mapping.Mapper;
import org.eclipse.microprofile.config.ConfigProvider;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@org.mapstruct.Mapper
public interface FileDtoImplMapper extends Mapper<FileImpl, FileDtoImpl> {

	@AfterMapping
	default void listenAfter(FileImpl source, @MappingTarget FileDtoImpl target) {
		target.set__audit__(source.get__audit__());
		setLinks(source, target);
	}
	
	static void setLinks(FileImpl source,FileDtoImpl target) {
		String context = ConfigProvider.getConfig().getOptionalValue("quarkus.resteasy.path", String.class).orElse("");
		if(!context.endsWith("/"))
			context = context+"/";
		target.downloadLink = context+FileService.PATH+"/"+String.format(FileService.DOWNLOAD_PATH_FORMAT, source.getIdentifier());
		//Link downloadLink = FileServiceImpl.buildDownloadLink(persistence.getIdentifier());
		//service.setDownloadLink(downloadLink.getValue());
		//service.get__links__(Boolean.TRUE).add(downloadLink);
	}
}
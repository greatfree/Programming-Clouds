package edu.chainnet.s3;

/*
 * The initial XML files are saved in the program. 07/20/2020, Bing Li
 */

// Created: 07/20/2020, Bing Li
class InitXML
{
//	public final static String S3_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Config>\n\t\t<RegistryIP>127.0.0.1</RegistryIP>\n\t\t<RegistryPort>8941</RegistryPort>\n</S3Config>\n";
	public final static String S3_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Config>\n\t<RegistryIP>192.168.3.17</RegistryIP>\n\t<RegistryPort>8941</RegistryPort>\n</S3Config>\n";

//	public final static String CLIENT_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Client>\n\t<ClientFile>/home/libing/Temp/mov.mp4</ClientFile>\n\t<K>5</K>\n\t<SliceSize>500000</SliceSize>\n</S3Client>\n";
//	public final static String CLIENT_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Client>\n\t<ClientFile>/home/libing/Temp/mov.mp4</ClientFile>\n\t<K>5</K>\n\t<SliceSize>20000</SliceSize>\n</S3Client>\n";
	public final static String CLIENT_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Client>\n\t<ClientFile>/home/libing/Temp/mov.mp4</ClientFile>\n\t<K>5</K>\n\t<SliceSize>80000</SliceSize>\n</S3Client>\n";
//	public final static String CLIENT_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<S3Client>\n\t\t<ClientFile>/home/libing/Temp/pic.jpg</ClientFile>\n\t\t<K>5</K>\n\t\t<SliceSize>20000</SliceSize>\n</S3Client>\n";
	
	public final static String META_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MetaConfig>\n\t<MetaPath>/home/libing/Temp/Meta/</MetaPath>\n</MetaConfig>\n";
	
	public final static String STORAGE_CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Storage>\n\t<SSSPath>/home/libing/Temp/Storage/</SSSPath>\n\t<FilePath>/home/libing/Temp/SSFiles/</FilePath>\n\t<Replicas>2</Replicas>\n\t<PartitionsPath>/home/libing/Temp/Storage/Partitions/</PartitionsPath>\n</Storage>\n";
}

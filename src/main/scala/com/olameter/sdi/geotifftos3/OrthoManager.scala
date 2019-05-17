package com.olameter.sdi.geotifftos3

import java.io.{ByteArrayOutputStream, ByteArrayInputStream, ObjectOutputStream, FileInputStream, File}

/** Main controller of the Imagery Service
*/
class OrthoManager {
    /**
     * FIXME Constructor method to instantiate
     * * OrthoRegistry
     * * OrthoFileManager
     */

    /**
     * FIXME : Do not implement now
     */
     def insert(orthoOutBytesStream : ByteArrayOutputStream ) = {
         //Save the
     }
    
    /**
     * FIXME: configure auth for minio connexion
     * 
     */
    def load(hostname: String, bucket: String, path: String) = {
        // val registry = new OrthoRegistry
        val ingestOrtho = new IngestOrtho

        //Insert the orth into the OrthoRegistry
        // ====> commented because im still have issues with the ignite persistence
        // val savedOrtho = registry.insert(hostname, bucket, path)

        //Tile the ortho
        // ====> commented for the same reason as above
        // ingestOrtho.sparkInitLocalSlicer(savedOrtho.storage_path, savedOrtho.storage_bucket)
        ingestOrtho.sparkInitLocalSlicer(path, bucket)
    }
    
    /**
     *
     */
    def get(ortho_name: String) = {
    }
}
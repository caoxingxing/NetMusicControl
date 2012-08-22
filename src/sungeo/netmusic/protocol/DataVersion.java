// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Version.proto

package sungeo.netmusic.protocol;

public final class DataVersion {
  private DataVersion() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class Version extends
      com.google.protobuf.GeneratedMessage {
    // Use Version.newBuilder() to construct.
    private Version() {
      initFields();
    }
    private Version(boolean noInit) {}
    
    private static final Version defaultInstance;
    public static Version getDefaultInstance() {
      return defaultInstance;
    }
    
    public Version getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return sungeo.netmusic.protocol.DataVersion.internal_static_Version_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return sungeo.netmusic.protocol.DataVersion.internal_static_Version_fieldAccessorTable;
    }
    
    // optional int32 data_version = 1;
    public static final int DATA_VERSION_FIELD_NUMBER = 1;
    private boolean hasDataVersion;
    private int dataVersion_ = 0;
    public boolean hasDataVersion() { return hasDataVersion; }
    public int getDataVersion() { return dataVersion_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasDataVersion()) {
        output.writeInt32(1, getDataVersion());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasDataVersion()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getDataVersion());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static sungeo.netmusic.protocol.DataVersion.Version parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(sungeo.netmusic.protocol.DataVersion.Version prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private sungeo.netmusic.protocol.DataVersion.Version result;
      
      // Construct using sungeo.netmusic.protocol.DataVersion.Version.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new sungeo.netmusic.protocol.DataVersion.Version();
        return builder;
      }
      
      protected sungeo.netmusic.protocol.DataVersion.Version internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new sungeo.netmusic.protocol.DataVersion.Version();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return sungeo.netmusic.protocol.DataVersion.Version.getDescriptor();
      }
      
      public sungeo.netmusic.protocol.DataVersion.Version getDefaultInstanceForType() {
        return sungeo.netmusic.protocol.DataVersion.Version.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public sungeo.netmusic.protocol.DataVersion.Version build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private sungeo.netmusic.protocol.DataVersion.Version buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public sungeo.netmusic.protocol.DataVersion.Version buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        sungeo.netmusic.protocol.DataVersion.Version returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof sungeo.netmusic.protocol.DataVersion.Version) {
          return mergeFrom((sungeo.netmusic.protocol.DataVersion.Version)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(sungeo.netmusic.protocol.DataVersion.Version other) {
        if (other == sungeo.netmusic.protocol.DataVersion.Version.getDefaultInstance()) return this;
        if (other.hasDataVersion()) {
          setDataVersion(other.getDataVersion());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setDataVersion(input.readInt32());
              break;
            }
          }
        }
      }
      
      
      // optional int32 data_version = 1;
      public boolean hasDataVersion() {
        return result.hasDataVersion();
      }
      public int getDataVersion() {
        return result.getDataVersion();
      }
      public Builder setDataVersion(int value) {
        result.hasDataVersion = true;
        result.dataVersion_ = value;
        return this;
      }
      public Builder clearDataVersion() {
        result.hasDataVersion = false;
        result.dataVersion_ = 0;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:Version)
    }
    
    static {
      defaultInstance = new Version(true);
      sungeo.netmusic.protocol.DataVersion.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:Version)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_Version_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_Version_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rVersion.proto\"\037\n\007Version\022\024\n\014data_versi" +
      "on\030\001 \001(\005B\'\n\030sungeo.netmusic.protocolB\013Da" +
      "taVersion"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_Version_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_Version_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_Version_descriptor,
              new java.lang.String[] { "DataVersion", },
              sungeo.netmusic.protocol.DataVersion.Version.class,
              sungeo.netmusic.protocol.DataVersion.Version.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
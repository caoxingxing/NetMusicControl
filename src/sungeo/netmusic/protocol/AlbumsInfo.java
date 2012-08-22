// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: AlbumsInfo.proto

package sungeo.netmusic.protocol;

public final class AlbumsInfo {
  private AlbumsInfo() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class AlbumListOfSingleCat extends
      com.google.protobuf.GeneratedMessage {
    // Use AlbumListOfSingleCat.newBuilder() to construct.
    private AlbumListOfSingleCat() {
      initFields();
    }
    private AlbumListOfSingleCat(boolean noInit) {}
    
    private static final AlbumListOfSingleCat defaultInstance;
    public static AlbumListOfSingleCat getDefaultInstance() {
      return defaultInstance;
    }
    
    public AlbumListOfSingleCat getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return sungeo.netmusic.protocol.AlbumsInfo.internal_static_AlbumListOfSingleCat_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return sungeo.netmusic.protocol.AlbumsInfo.internal_static_AlbumListOfSingleCat_fieldAccessorTable;
    }
    
    public static final class AlbumInfo extends
        com.google.protobuf.GeneratedMessage {
      // Use AlbumInfo.newBuilder() to construct.
      private AlbumInfo() {
        initFields();
      }
      private AlbumInfo(boolean noInit) {}
      
      private static final AlbumInfo defaultInstance;
      public static AlbumInfo getDefaultInstance() {
        return defaultInstance;
      }
      
      public AlbumInfo getDefaultInstanceForType() {
        return defaultInstance;
      }
      
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return sungeo.netmusic.protocol.AlbumsInfo.internal_static_AlbumListOfSingleCat_AlbumInfo_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return sungeo.netmusic.protocol.AlbumsInfo.internal_static_AlbumListOfSingleCat_AlbumInfo_fieldAccessorTable;
      }
      
      // optional int32 album_id = 1;
      public static final int ALBUM_ID_FIELD_NUMBER = 1;
      private boolean hasAlbumId;
      private int albumId_ = 0;
      public boolean hasAlbumId() { return hasAlbumId; }
      public int getAlbumId() { return albumId_; }
      
      // optional string album_name = 2;
      public static final int ALBUM_NAME_FIELD_NUMBER = 2;
      private boolean hasAlbumName;
      private java.lang.String albumName_ = "";
      public boolean hasAlbumName() { return hasAlbumName; }
      public java.lang.String getAlbumName() { return albumName_; }
      
      private void initFields() {
      }
      public final boolean isInitialized() {
        return true;
      }
      
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        getSerializedSize();
        if (hasAlbumId()) {
          output.writeInt32(1, getAlbumId());
        }
        if (hasAlbumName()) {
          output.writeString(2, getAlbumName());
        }
        getUnknownFields().writeTo(output);
      }
      
      private int memoizedSerializedSize = -1;
      public int getSerializedSize() {
        int size = memoizedSerializedSize;
        if (size != -1) return size;
      
        size = 0;
        if (hasAlbumId()) {
          size += com.google.protobuf.CodedOutputStream
            .computeInt32Size(1, getAlbumId());
        }
        if (hasAlbumName()) {
          size += com.google.protobuf.CodedOutputStream
            .computeStringSize(2, getAlbumName());
        }
        size += getUnknownFields().getSerializedSize();
        memoizedSerializedSize = size;
        return size;
      }
      
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data, extensionRegistry)
                 .buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data, extensionRegistry)
                 .buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return newBuilder().mergeFrom(input).buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return newBuilder().mergeFrom(input, extensionRegistry)
                 .buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        Builder builder = newBuilder();
        if (builder.mergeDelimitedFrom(input)) {
          return builder.buildParsed();
        } else {
          return null;
        }
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseDelimitedFrom(
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
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return newBuilder().mergeFrom(input).buildParsed();
      }
      public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return newBuilder().mergeFrom(input, extensionRegistry)
                 .buildParsed();
      }
      
      public static Builder newBuilder() { return Builder.create(); }
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo prototype) {
        return newBuilder().mergeFrom(prototype);
      }
      public Builder toBuilder() { return newBuilder(this); }
      
      public static final class Builder extends
          com.google.protobuf.GeneratedMessage.Builder<Builder> {
        private sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo result;
        
        // Construct using sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.newBuilder()
        private Builder() {}
        
        private static Builder create() {
          Builder builder = new Builder();
          builder.result = new sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo();
          return builder;
        }
        
        protected sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo internalGetResult() {
          return result;
        }
        
        public Builder clear() {
          if (result == null) {
            throw new IllegalStateException(
              "Cannot call clear() after build().");
          }
          result = new sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo();
          return this;
        }
        
        public Builder clone() {
          return create().mergeFrom(result);
        }
        
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.getDescriptor();
        }
        
        public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo getDefaultInstanceForType() {
          return sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.getDefaultInstance();
        }
        
        public boolean isInitialized() {
          return result.isInitialized();
        }
        public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo build() {
          if (result != null && !isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return buildPartial();
        }
        
        private sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo buildParsed()
            throws com.google.protobuf.InvalidProtocolBufferException {
          if (!isInitialized()) {
            throw newUninitializedMessageException(
              result).asInvalidProtocolBufferException();
          }
          return buildPartial();
        }
        
        public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo buildPartial() {
          if (result == null) {
            throw new IllegalStateException(
              "build() has already been called on this Builder.");
          }
          sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo returnMe = result;
          result = null;
          return returnMe;
        }
        
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo) {
            return mergeFrom((sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }
        
        public Builder mergeFrom(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo other) {
          if (other == sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.getDefaultInstance()) return this;
          if (other.hasAlbumId()) {
            setAlbumId(other.getAlbumId());
          }
          if (other.hasAlbumName()) {
            setAlbumName(other.getAlbumName());
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
                setAlbumId(input.readInt32());
                break;
              }
              case 18: {
                setAlbumName(input.readString());
                break;
              }
            }
          }
        }
        
        
        // optional int32 album_id = 1;
        public boolean hasAlbumId() {
          return result.hasAlbumId();
        }
        public int getAlbumId() {
          return result.getAlbumId();
        }
        public Builder setAlbumId(int value) {
          result.hasAlbumId = true;
          result.albumId_ = value;
          return this;
        }
        public Builder clearAlbumId() {
          result.hasAlbumId = false;
          result.albumId_ = 0;
          return this;
        }
        
        // optional string album_name = 2;
        public boolean hasAlbumName() {
          return result.hasAlbumName();
        }
        public java.lang.String getAlbumName() {
          return result.getAlbumName();
        }
        public Builder setAlbumName(java.lang.String value) {
          if (value == null) {
    throw new NullPointerException();
  }
  result.hasAlbumName = true;
          result.albumName_ = value;
          return this;
        }
        public Builder clearAlbumName() {
          result.hasAlbumName = false;
          result.albumName_ = getDefaultInstance().getAlbumName();
          return this;
        }
        
        // @@protoc_insertion_point(builder_scope:AlbumListOfSingleCat.AlbumInfo)
      }
      
      static {
        defaultInstance = new AlbumInfo(true);
        sungeo.netmusic.protocol.AlbumsInfo.internalForceInit();
        defaultInstance.initFields();
      }
      
      // @@protoc_insertion_point(class_scope:AlbumListOfSingleCat.AlbumInfo)
    }
    
    // optional int32 cat_id = 1;
    public static final int CAT_ID_FIELD_NUMBER = 1;
    private boolean hasCatId;
    private int catId_ = 0;
    public boolean hasCatId() { return hasCatId; }
    public int getCatId() { return catId_; }
    
    // repeated .AlbumListOfSingleCat.AlbumInfo album_info = 2;
    public static final int ALBUM_INFO_FIELD_NUMBER = 2;
    private java.util.List<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo> albumInfo_ =
      java.util.Collections.emptyList();
    public java.util.List<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo> getAlbumInfoList() {
      return albumInfo_;
    }
    public int getAlbumInfoCount() { return albumInfo_.size(); }
    public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo getAlbumInfo(int index) {
      return albumInfo_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasCatId()) {
        output.writeInt32(1, getCatId());
      }
      for (sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo element : getAlbumInfoList()) {
        output.writeMessage(2, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasCatId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getCatId());
      }
      for (sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo element : getAlbumInfoList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseDelimitedFrom(
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
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat result;
      
      // Construct using sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat();
        return builder;
      }
      
      protected sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.getDescriptor();
      }
      
      public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat getDefaultInstanceForType() {
        return sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.albumInfo_ != java.util.Collections.EMPTY_LIST) {
          result.albumInfo_ =
            java.util.Collections.unmodifiableList(result.albumInfo_);
        }
        sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat) {
          return mergeFrom((sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat other) {
        if (other == sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.getDefaultInstance()) return this;
        if (other.hasCatId()) {
          setCatId(other.getCatId());
        }
        if (!other.albumInfo_.isEmpty()) {
          if (result.albumInfo_.isEmpty()) {
            result.albumInfo_ = new java.util.ArrayList<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo>();
          }
          result.albumInfo_.addAll(other.albumInfo_);
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
              setCatId(input.readInt32());
              break;
            }
            case 18: {
              sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.Builder subBuilder = sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAlbumInfo(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional int32 cat_id = 1;
      public boolean hasCatId() {
        return result.hasCatId();
      }
      public int getCatId() {
        return result.getCatId();
      }
      public Builder setCatId(int value) {
        result.hasCatId = true;
        result.catId_ = value;
        return this;
      }
      public Builder clearCatId() {
        result.hasCatId = false;
        result.catId_ = 0;
        return this;
      }
      
      // repeated .AlbumListOfSingleCat.AlbumInfo album_info = 2;
      public java.util.List<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo> getAlbumInfoList() {
        return java.util.Collections.unmodifiableList(result.albumInfo_);
      }
      public int getAlbumInfoCount() {
        return result.getAlbumInfoCount();
      }
      public sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo getAlbumInfo(int index) {
        return result.getAlbumInfo(index);
      }
      public Builder setAlbumInfo(int index, sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.albumInfo_.set(index, value);
        return this;
      }
      public Builder setAlbumInfo(int index, sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.Builder builderForValue) {
        result.albumInfo_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAlbumInfo(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.albumInfo_.isEmpty()) {
          result.albumInfo_ = new java.util.ArrayList<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo>();
        }
        result.albumInfo_.add(value);
        return this;
      }
      public Builder addAlbumInfo(sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.Builder builderForValue) {
        if (result.albumInfo_.isEmpty()) {
          result.albumInfo_ = new java.util.ArrayList<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo>();
        }
        result.albumInfo_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAlbumInfo(
          java.lang.Iterable<? extends sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo> values) {
        if (result.albumInfo_.isEmpty()) {
          result.albumInfo_ = new java.util.ArrayList<sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo>();
        }
        super.addAll(values, result.albumInfo_);
        return this;
      }
      public Builder clearAlbumInfo() {
        result.albumInfo_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:AlbumListOfSingleCat)
    }
    
    static {
      defaultInstance = new AlbumListOfSingleCat(true);
      sungeo.netmusic.protocol.AlbumsInfo.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:AlbumListOfSingleCat)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_AlbumListOfSingleCat_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_AlbumListOfSingleCat_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_AlbumListOfSingleCat_AlbumInfo_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_AlbumListOfSingleCat_AlbumInfo_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\020AlbumsInfo.proto\"\216\001\n\024AlbumListOfSingle" +
      "Cat\022\016\n\006cat_id\030\001 \001(\005\0223\n\nalbum_info\030\002 \003(\0132" +
      "\037.AlbumListOfSingleCat.AlbumInfo\0321\n\tAlbu" +
      "mInfo\022\020\n\010album_id\030\001 \001(\005\022\022\n\nalbum_name\030\002 " +
      "\001(\tB&\n\030sungeo.netmusic.protocolB\nAlbumsI" +
      "nfo"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_AlbumListOfSingleCat_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_AlbumListOfSingleCat_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_AlbumListOfSingleCat_descriptor,
              new java.lang.String[] { "CatId", "AlbumInfo", },
              sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.class,
              sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.Builder.class);
          internal_static_AlbumListOfSingleCat_AlbumInfo_descriptor =
            internal_static_AlbumListOfSingleCat_descriptor.getNestedTypes().get(0);
          internal_static_AlbumListOfSingleCat_AlbumInfo_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_AlbumListOfSingleCat_AlbumInfo_descriptor,
              new java.lang.String[] { "AlbumId", "AlbumName", },
              sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.class,
              sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo.Builder.class);
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
# automatically generated by the FlatBuffers compiler, do not modify

# namespace: vision

import flatbuffers

class Image(object):
    __slots__ = ['_tab']

    @classmethod
    def GetRootAsImage(cls, buf, offset):
        n = flatbuffers.encode.Get(flatbuffers.packer.uoffset, buf, offset)
        x = Image()
        x.Init(buf, n + offset)
        return x

    # Image
    def Init(self, buf, pos):
        self._tab = flatbuffers.table.Table(buf, pos)

    # Image
    def Data(self, j):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            a = self._tab.Vector(o)
            return self._tab.Get(flatbuffers.number_types.Uint8Flags, a + flatbuffers.number_types.UOffsetTFlags.py_type(j * 1))
        return 0

    # Image
    def DataAsNumpy(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            return self._tab.GetVectorAsNumpy(flatbuffers.number_types.Uint8Flags, o)
        return 0

    # Image
    def DataLength(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            return self._tab.VectorLen(o)
        return 0

    # Image
    def Width(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
        if o != 0:
            return self._tab.Get(flatbuffers.number_types.Uint32Flags, o + self._tab.Pos)
        return 0

    # Image
    def Height(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(8))
        if o != 0:
            return self._tab.Get(flatbuffers.number_types.Uint32Flags, o + self._tab.Pos)
        return 0

def ImageStart(builder): builder.StartObject(3)
def ImageAddData(builder, data): builder.PrependUOffsetTRelativeSlot(0, flatbuffers.number_types.UOffsetTFlags.py_type(data), 0)
def ImageStartDataVector(builder, numElems): return builder.StartVector(1, numElems, 1)
def ImageAddWidth(builder, width): builder.PrependUint32Slot(1, width, 0)
def ImageAddHeight(builder, height): builder.PrependUint32Slot(2, height, 0)
def ImageEnd(builder): return builder.EndObject()

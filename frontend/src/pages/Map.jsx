import {
  Map as KakaoMap,
  MarkerClusterer,
  MapMarker,
} from 'react-kakao-maps-sdk';
import { useState, useEffect, useRef } from 'react';
import styled from 'styled-components';
import BottomSheet from '../components/common/BottomSheet';
import { getZipsaPositionWithinTwoKilos } from '../apis/api/map';

const Wrapper = styled.div`
  position: relative;
  width: 320px;
  min-height: 568px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 11px;
  background-color: ${({ theme }) => theme.colors.primary};
  font-size: 25px;
  font-weight: 300;
  white-space: pre-wrap;
`;

// Map 컴포넌트를 띄울 때, lat값과 lng값이 입력되어야 함.
function Map() {
  const [isOpen, setIsOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false); // 상세 정보
  const [positions, setPositions] = useState([]);
  const [targetCluster, setTargetCluster] = useState();
  const modalRef = useRef(null);

  // 중심 좌표 기준으로 2km 이내의 집사들의 lat, lng 값을 받아옴
  useEffect(() => {
    getZipsaPositionWithinTwoKilos(2).then(response => setPositions(response));
  }, []);

  // bottom sheet 영역 이외의 부분을 클릭 시 모달 isOpen 변경
  useEffect(() => {
    const closeBottomSheet = event => {
      isOpen &&
        modalRef.current &&
        !modalRef.current.contains(event.target) &&
        setIsOpen(false);
    };
    document.addEventListener('mousedown', closeBottomSheet);
    return () => {
      document.removeEventListener('mousedown', closeBottomSheet);
    };
  }, [isOpen]);

  const onClusterclick = (_target, cluster) => {
    const { Ma, La } = cluster.getCenter(); // Ma: 위도, La: 경도
    setTargetCluster({ lat: Ma, lng: La });
    setIsOpen(true);
    setIsDetailOpen(false);
  };

  return (
    <Wrapper>
      <KakaoMap
        center={{ lat: 37.506320759000715, lng: 127.05368251210247 }}
        style={{
          width: '320px',
          height: '568px',
        }}
        level={6}
        disableDoubleClickZoom={true}
        zoomable={false}
        draggable={false}
      >
        <MarkerClusterer
          averageCenter={true}
          minLevel={6}
          minClusterSize={1}
          calculator={[1]}
          styles={[
            {
              width: '70px',
              height: '70px',
              background: 'rgba(232, 46, 28, .4)',
              borderRadius: '50%',
              textAlign: 'center',
              fontWeight: '500',
              lineHeight: '70px',
            },
          ]}
          disableClickZoom={true}
          onClusterclick={onClusterclick}
        >
          {positions.map((pos, index) => (
            <MapMarker
              key={`${pos.lat}-${pos.lng}-${index}`}
              position={{
                lat: pos.lat,
                lng: pos.lng,
              }}
            />
          ))}
        </MarkerClusterer>
      </KakaoMap>

      <BottomSheet
        isOpen={isOpen}
        isDetailOpen={isDetailOpen}
        setIsDetailOpen={setIsDetailOpen}
        ref={modalRef}
        onClick={() => {
          setIsOpen(false);
        }}
        targetCluster={targetCluster}
      ></BottomSheet>
    </Wrapper>
  );
}

export default Map;

package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
// final 키워드가 있는 것 만 찾아서 주입한다.
@RequiredArgsConstructor
public class MemberService {

    // field injection
    // @Autowired
    // final 을 추가하면 컴파일 시점에 설정하지 않는 오류를 체크 할 수 있음
    private final MemberRepository memberRepository;

    // 생성자 주입 방식
    // test case 작성시에 생성자에 넘겨주어야 하기 때문에 명확히 놓치지 않고 알 수 가 있다.
    // 하나인 경우에는 autowired 생략 가능
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입 ( readOnly false 가 기본 )
    @Transactional
    public Long join(Member member) {
        // Validation ( 중복 검증 )
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 실무에서는 동시에 가입하는 상황을 고려해서 validation 을 추가로 처리해야한다. 유니크 제약 조건 추가 !
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 성능 최적화 ( 조회에서 )
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
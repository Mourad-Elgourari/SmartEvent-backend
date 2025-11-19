package com.congress.event.service;

import com.congress.event.model.Member;
import com.congress.event.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member memberDetails) {
        return memberRepository.findById(id)
                .map(member -> {
                    member.setBirthday(memberDetails.getBirthday());
                    member.setPhoneNumber(memberDetails.getPhoneNumber());
                    member.setGender(memberDetails.getGender());
                    member.setCountry(memberDetails.getCountry());
                    member.setCity(memberDetails.getCity());
                    member.setFavoris(memberDetails.getFavoris());
                    // inherited fields from User are also available
                    member.setEmail(memberDetails.getEmail());
                    member.setUsername(memberDetails.getUsername());
                    return memberRepository.save(member);
                })
                .orElseThrow(() -> new RuntimeException("Member not found with id " + id));
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
